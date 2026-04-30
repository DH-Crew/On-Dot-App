package com.ondot.main.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import com.dh.ondot.presentation.ui.theme.ERROR_SET_MAP_PROVIDER
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_ALARM
import com.dh.ondot.presentation.ui.theme.NOTIFICATION_TITLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_DELETE_SCHEDULE
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.request.localNotification.LocalNotificationRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.DirectionsOpener
import com.ondot.domain.service.LocalNotificationScheduler
import com.ondot.domain.service.ScheduleAlarmManager
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val memberRepository: MemberRepository,
    private val scheduleAlarmManager: ScheduleAlarmManager,
    private val notificationScheduler: LocalNotificationScheduler,
    private val directionsOpener: DirectionsOpener,
) : BaseViewModel<HomeUiState>(HomeUiState()) {
    private val logger = Logger.withTag("HomeVMl")
    private var mapProvider = MapProvider.KAKAO
    private var lastScheduledAlarms: Set<Long> = emptySet()

    private var remainingTimeJob: Job? = null
    private var earliestAlarmAt: String? = null

    init {
        needsChooseProvider()
        observeMapProvider()
    }

    private fun needsChooseProvider() {
        viewModelScope.launch {
            memberRepository.needsChooseProvider().collect {
                logger.e { "needsChooseProvider: $it" }
                updateState(uiState.value.copy(needsChooseProvider = it))
            }
        }
    }

    private fun observeMapProvider() =
        viewModelScope.launch {
            memberRepository.getLocalMapProvider().collect {
                logger.e { "mapProvider: $it" }
                mapProvider = it
            }
        }

    fun onToggle() {
        updateStateSync(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(
        id: Long,
        isEnabled: Boolean,
    ) {
        val previousList = uiState.value.scheduleList
        val originalSchedule = previousList.find { it.scheduleId == id } ?: return

        val updatedSchedule =
            originalSchedule.copy(
                hasActiveAlarm = isEnabled,
                departureAlarm = originalSchedule.departureAlarm.copy(enabled = isEnabled),
                preparationAlarm = originalSchedule.preparationAlarm.copy(enabled = isEnabled),
            )

        val updatedList =
            previousList.map {
                if (it.scheduleId == id) updatedSchedule else it
            }

        updateStateSync(uiState.value.copy(scheduleList = updatedList))

        viewModelScope.launch {
            try {
                // 로컬/OS 알람 반영을 먼저 기다림
                scheduleAlarmManager.applyToggle(
                    original = originalSchedule,
                    enabled = isEnabled,
                )

                // 서버 PATCH도 같은 coroutine에서 순차 처리
                var remoteError: Throwable? = null
                scheduleRepository
                    .toggleAlarm(
                        scheduleId = id,
                        request = ToggleAlarmRequest(isEnabled = isEnabled),
                    ).collect { result ->
                        result.onFailure { remoteError = it }
                    }

                remoteError?.let { throw it }

                // 성공 이후에만 lastScheduledAlarms 반영
                lastScheduledAlarms =
                    if (isEnabled) {
                        lastScheduledAlarms +
                            setOf(
                                originalSchedule.departureAlarm.alarmId,
                                originalSchedule.preparationAlarm.alarmId,
                            )
                    } else {
                        lastScheduledAlarms -
                            setOf(
                                originalSchedule.departureAlarm.alarmId,
                                originalSchedule.preparationAlarm.alarmId,
                            )
                    }

                getScheduleList()
            } catch (t: Throwable) {
                logger.e(t) { "알람 토글 처리 실패" }

                // UI rollback
                updateStateSync(uiState.value.copy(scheduleList = previousList))

                // OS 알람도 원래 상태로 복구 시도
                runCatching {
                    scheduleAlarmManager.applyToggle(
                        original = originalSchedule,
                        enabled = originalSchedule.hasActiveAlarm,
                    )
                }.onFailure { rollbackError ->
                    logger.e(rollbackError) { "알람 토글 rollback 실패" }
                }

                ToastManager.show(
                    message = ERROR_UPDATE_ALARM,
                    type = ToastType.ERROR,
                )
            }
        }
    }

    /**----------------------------------------------스케줄 조회---------------------------------------------*/

    fun getScheduleList() {
        viewModelScope.launch {
            scheduleRepository.getScheduleList().collect { result ->
                result
                    .onSuccess { scheduleList ->
                        onSuccessGetScheduleList(scheduleList)
                    }.onFailure { throwable ->
                        onFailureGetScheduleList(throwable)
                    }
            }
        }
    }

    private suspend fun onSuccessGetScheduleList(result: ScheduleList) {
        earliestAlarmAt = result.earliestAlarmAt.ifBlank { null }

        val remainingTime =
            if (result.earliestAlarmAt.isNotBlank()) {
                DateTimeFormatter.calculateRemainingTime(result.earliestAlarmAt)
            } else {
                Triple(-1, -1, -1)
            }

        updateStateSync(
            uiState.value.copy(
                remainingTime = remainingTime,
                scheduleList = result.scheduleList,
                earliestScheduleId = result.earliestScheduleId,
            ),
        )

        startRemainingTimeTicker()

        lastScheduledAlarms =
            scheduleAlarmManager.syncAll(
                schedules = result.scheduleList,
                previouslyScheduledAlarmIds = lastScheduledAlarms,
            )

        scheduleNotification(result.scheduleList)
    }

    private fun scheduleNotification(schedules: List<Schedule>) {
        val twoMinutesMillis = 2 * 60 * 1000L

        schedules.forEach { schedule ->
            if (schedule.preparationNote.isNotBlank()) {
                notificationScheduler.schedule(
                    request =
                        LocalNotificationRequest(
                            id = schedule.scheduleId.toString(),
                            title = NOTIFICATION_TITLE,
                            body = schedule.preparationNote,
                            triggerAtMillis =
                                DateTimeFormatter.isoStringToEpochMillis(schedule.departureAlarm.triggeredAt) - twoMinutesMillis,
                        ),
                )
            }
        }
    }

    private fun onFailureGetScheduleList(throwable: Throwable) {
        logger.e { throwable.message.toString() }

        viewModelScope.launch { ToastManager.show(message = ERROR_GET_SCHEDULE_LIST, type = ToastType.ERROR) }
    }

    fun setMapProvider(mapProvider: MapProvider) {
        viewModelScope.launch {
            memberRepository.updateMapProvider(request = MapProviderRequest(mapProvider)).collect {
                resultResponse(
                    it,
                    {
                        updateState(uiState.value.copy(needsChooseProvider = false))
                    },
                    {
                        logger.e { it.message.toString() }
                        viewModelScope.launch { ToastManager.show(message = ERROR_SET_MAP_PROVIDER, type = ToastType.ERROR) }
                    },
                )
            }
        }
    }

    /**----------------------------------------------일정 삭제---------------------------------------------*/

    fun deleteSchedule(scheduleId: Long) {
        val currentList = uiState.value.scheduleList
        val targetSchedule = currentList.find { it.scheduleId == scheduleId } ?: return
        val newList = currentList.filter { it.scheduleId != scheduleId }
        val job =
            viewModelScope.launch {
                delay(2000)
                scheduleRepository.deleteSchedule(scheduleId).collect {
                    resultResponse(it, { onSuccessDeleteSchedule(scheduleId) }, ::onFailDeleteSchedule)
                }
            }

        updateStateSync(uiState.value.copy(scheduleList = newList))

        viewModelScope.launch {
            try {
                if (targetSchedule.hasActiveAlarm) {
                    scheduleAlarmManager.cancel(targetSchedule)
                }

                ToastManager.show(
                    message = SUCCESS_DELETE_SCHEDULE,
                    type = ToastType.DELETE,
                    callback = {
                        job.cancel()
                        updateStateSync(uiState.value.copy(scheduleList = currentList))

                        viewModelScope.launch {
                            runCatching {
                                if (targetSchedule.hasActiveAlarm) {
                                    scheduleAlarmManager.schedule(targetSchedule)
                                }
                            }.onFailure { error ->
                                logger.e(error) { "삭제 undo 시 알람 복구 실패" }
                            }
                        }
                    },
                )
            } catch (t: Throwable) {
                logger.e(t) { "일정 삭제 전 알람 취소 실패" }
                job.cancel()
                updateStateSync(uiState.value.copy(scheduleList = currentList))
                ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
            }
        }
    }

    private fun onSuccessDeleteSchedule(scheduleId: Long) {
        notificationScheduler.cancel(scheduleId.toString())
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.filter { it.scheduleId != scheduleId }))
        getScheduleList()
    }

    private fun onFailDeleteSchedule(e: Throwable) {
        logger.e { e.message.toString() }
        viewModelScope.launch {
            ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
            getScheduleList()
        }
    }

    /**--------------------------------------------경로 안내-----------------------------------------------*/

    fun openDirections(id: Long) {
        val schedule = uiState.value.scheduleList.first { it.scheduleId == id }

        directionsOpener.openDirections(
            startLat = schedule.startLatitude,
            startLng = schedule.startLongitude,
            endLat = schedule.endLatitude,
            endLng = schedule.endLongitude,
            provider = mapProvider,
            startName = "출발지",
            endName = "도착지",
        )
    }

    /**--------------------------------------------기타 유틸-----------------------------------------------*/

    private fun startRemainingTimeTicker() {
        val target = earliestAlarmAt
        if (target == null) {
            remainingTimeJob?.cancel()
            remainingTimeJob = null
            return
        }

        remainingTimeJob?.cancel()
        remainingTimeJob =
            viewModelScope.launch {
                while (isActive) {
                    val newRemaining = DateTimeFormatter.calculateRemainingTime(target)

                    // 이미 0 이하라면 한 번 업데이트만 하고 종료
                    if (newRemaining.first <= 0 && newRemaining.second <= 0 && newRemaining.third <= 0) {
                        updateStateSync(uiState.value.copy(remainingTime = newRemaining))
                        break
                    }

                    // 매 분 갱신
                    updateStateSync(uiState.value.copy(remainingTime = newRemaining))
                    delay(60_000L)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        remainingTimeJob?.cancel()
    }
}
