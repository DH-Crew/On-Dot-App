package com.dh.ondot.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import com.dh.ondot.presentation.ui.theme.ERROR_SET_MAP_PROVIDER
import com.dh.ondot.presentation.ui.theme.NOTIFICATION_TITLE
import com.dh.ondot.presentation.ui.theme.SUCCESS_DELETE_SCHEDULE
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.ToggleAlarmRequest
import com.ondot.domain.model.request.local_notification.LocalNotificationRequest
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleList
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.LocalNotificationScheduler
import com.ondot.util.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val memberRepository: MemberRepository,
    private val alarmScheduler: AlarmScheduler,
    private val analyticsManager: AnalyticsManager,
    private val notificationScheduler: LocalNotificationScheduler
) : BaseViewModel<HomeUiState>(HomeUiState()) {
    private val logger = Logger.withTag("HomeViewModel")
    private var mapProvider = MapProvider.KAKAO

    private fun logGA(name: String, vararg params: Pair<String, Any?>) {
        val clean = params.toMap().filterValues { it != null }
        analyticsManager.logEvent(name, clean)
    }

    init {
        logGA("home_open")

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

    private fun observeMapProvider() = viewModelScope.launch {
        memberRepository.getLocalMapProvider().collect {
            logger.e { "mapProvider: $it" }
            mapProvider = it
        }
    }

    fun onToggle() {
        updateState(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(id: Long, isEnabled: Boolean) {
        logGA("schedule_alarm_toggle", "schedule_id" to id, "enabled" to isEnabled)

        val newList = uiState.value.scheduleList.map {
            if (it.scheduleId == id) {
                it.copy(
                    hasActiveAlarm = isEnabled,
                    departureAlarm = it.departureAlarm.copy(enabled = isEnabled),
                    preparationAlarm = it.preparationAlarm.copy(enabled = isEnabled)
                )
            } else {
                it
            }
        }

        updateState(uiState.value.copy(scheduleList = newList))

        if (isEnabled) processAlarms(newList)
        else cancelAlarms(id)

        viewModelScope.launch {
            scheduleRepository.toggleAlarm(scheduleId = id, request = ToggleAlarmRequest(isEnabled = isEnabled)).collect {
                resultResponse(it, {})
            }
        }
    }

    /**----------------------------------------------스케줄 조회---------------------------------------------*/

    fun getScheduleList() {
        logGA("schedule_list_request")

        viewModelScope.launch {
            scheduleRepository.getScheduleList().collect {
                resultResponse(it, ::onSuccessGetScheduleList, ::onFailureGetScheduleList)
            }
        }
    }

    private fun onSuccessGetScheduleList(result: ScheduleList) {
        val remainingTime = if (result.earliestAlarmAt.isNotBlank()) {
            DateTimeFormatter.calculateRemainingTime(result.earliestAlarmAt)
        } else { Triple(-1, -1, -1) }

        updateState(
            uiState.value.copy(
                remainingTime = remainingTime,
                scheduleList = result.scheduleList
            )
        )

        processAlarms(result.scheduleList)
        scheduleNotification(result.scheduleList)
    }

    private fun processAlarms(schedules: List<Schedule>) {
        viewModelScope.launch {
            // 알람 리스트 추출
            val alarmRingInfos = schedules.flatMap { schedule ->
                buildList {
                    if (schedule.preparationAlarm.enabled) {
                        add(
                            AlarmRingInfo(
                                alarm = schedule.preparationAlarm,
                                alarmType = AlarmType.Preparation,
                                appointmentAt = schedule.appointmentAt,
                                scheduleTitle = schedule.scheduleTitle,
                                scheduleId = schedule.scheduleId,
                                startLat = schedule.startLatitude,
                                startLng = schedule.startLongitude,
                                endLat = schedule.endLatitude,
                                endLng = schedule.endLongitude
                            )
                        )
                    }
                    add(
                        AlarmRingInfo(
                            alarm = schedule.departureAlarm,
                            alarmType = AlarmType.Departure,
                            appointmentAt = schedule.appointmentAt,
                            scheduleTitle = schedule.scheduleTitle,
                            scheduleId = schedule.scheduleId,
                            startLat = schedule.startLatitude,
                            startLng = schedule.startLongitude,
                            endLat = schedule.endLatitude,
                            endLng = schedule.endLongitude
                        )
                    )
                }
            }

            // 스케줄러 예약
            alarmRingInfos.forEach { info ->
                alarmScheduler.scheduleAlarm(info = info, mapProvider = mapProvider)
            }
        }
    }

    private fun scheduleNotification(schedules: List<Schedule>) {
        val twoMinutesMillis = 2 * 60 * 1000L

        schedules.forEach { schedule ->
            if (schedule.preparationNote.isNotBlank()) {
                notificationScheduler.schedule(
                    request = LocalNotificationRequest(
                        id = schedule.scheduleId.toString(),
                        title = NOTIFICATION_TITLE,
                        body = schedule.preparationNote,
                        triggerAtMillis = DateTimeFormatter.isoStringToEpochMillis(schedule.departureAlarm.triggeredAt) - twoMinutesMillis
                    )
                )
            }
        }
    }

    private fun onFailureGetScheduleList(throwable: Throwable) {
        logger.e { throwable.message.toString() }

        viewModelScope.launch { ToastManager.show(message = ERROR_GET_SCHEDULE_LIST, type = ToastType.ERROR) }
    }

    fun setMapProvider(mapProvider: MapProvider) {
        logGA("map_provider_select", "provider" to mapProvider.name.lowercase())

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
                    }
                )
            }
        }
    }

    /**----------------------------------------------일정 삭제---------------------------------------------*/

    fun deleteSchedule(scheduleId: Long) {
        val curList = uiState.value.scheduleList
        val newList = uiState.value.scheduleList.filter { it.scheduleId != scheduleId }
        val job = viewModelScope.launch {
            delay(2000)
            scheduleRepository.deleteSchedule(scheduleId).collect {
                resultResponse(it, { onSuccessDeleteSchedule(scheduleId) }, ::onFailDeleteSchedule)
            }
        }

        updateState(uiState.value.copy(scheduleList = newList))

        viewModelScope.launch {
            ToastManager.show(
                message = SUCCESS_DELETE_SCHEDULE,
                type = ToastType.DELETE,
                callback = {
                    job.cancel()
                    updateState(uiState.value.copy(scheduleList = curList))
                }
            )
        }

        logGA("schedule_delete_request", "schedule_id" to scheduleId)
    }

    private fun onSuccessDeleteSchedule(scheduleId: Long) {
        cancelAlarms(scheduleId)
        notificationScheduler.cancel(scheduleId.toString())
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.filter { it.scheduleId != scheduleId }))
        getScheduleList()
    }

    private fun onFailDeleteSchedule(e: Throwable) {
        logger.e { e.message.toString() }
        viewModelScope.launch { ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR) }
    }

    /**--------------------------------------------알람 취소-----------------------------------------------*/

    /**홈 화면에서 일정을 삭제할 때 스케줄링된 알람도 함께 삭제*/
    private fun cancelAlarms(scheduleId: Long) {
        val schedule = uiState.value.scheduleList.find { it.scheduleId == scheduleId }
        schedule?.let {
            alarmScheduler.cancelAlarm(it.departureAlarm.alarmId)
            alarmScheduler.cancelAlarm(it.preparationAlarm.alarmId)
            logGA(
                "alarms_cancelled_for_schedule",
                "schedule_id" to scheduleId,
                "departure_alarm_id" to it.departureAlarm.alarmId,
                "preparation_alarm_id" to it.preparationAlarm.alarmId
            )
        }
    }
}