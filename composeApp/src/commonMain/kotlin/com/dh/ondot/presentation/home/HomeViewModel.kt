package com.dh.ondot.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.request.MapProviderRequest
import com.dh.ondot.domain.model.request.ToggleAlarmRequest
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.domain.model.response.ScheduleListResponse
import com.dh.ondot.domain.model.ui.AlarmRingInfo
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import com.dh.ondot.presentation.ui.theme.ERROR_SET_MAP_PROVIDER
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository,
    private val memberRepository: MemberRepository = ServiceLocator.memberRepository,
    private val alarmStorage: AlarmStorage = ServiceLocator.provideAlarmStorage(),
    private val alarmScheduler: AlarmScheduler = ServiceLocator.provideAlarmScheduler()
) : BaseViewModel<HomeUiState>(HomeUiState()) {
    private val logger = Logger.withTag("HomeViewModel")

    init {
        needsChooseProvider()
    }

    private fun needsChooseProvider() {
        viewModelScope.launch {
            memberRepository.needsChooseProvider().collect {
                logger.e { "needsChooseProvider: $it" }
                updateState(uiState.value.copy(needsChooseProvider = it))
            }
        }
    }

    fun onToggle() {
        updateState(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(id: Long, isEnabled: Boolean) {
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.map {
            if (it.scheduleId == id) {
                it.copy(
                    isEnabled = isEnabled,
                    departureAlarm = it.departureAlarm.copy(enabled = isEnabled),
                    preparationAlarm = it.preparationAlarm.copy(enabled = isEnabled)
                )
            } else {
                it
            }
        }))

        viewModelScope.launch {
            scheduleRepository.toggleAlarm(scheduleId = id, request = ToggleAlarmRequest(isEnabled = isEnabled)).collect {
                resultResponse(it, {})
            }
        }
    }

    fun getScheduleList() {
        viewModelScope.launch {
            scheduleRepository.getScheduleList().collect {
                resultResponse(it, ::onSuccessGetScheduleList, ::onFailureGetScheduleList)
            }
        }
    }

    private fun onSuccessGetScheduleList(result: ScheduleListResponse) {
        val remainingTime = if (result.earliestAlarmAt != null) {
            DateTimeFormatter.calculateRemainingTime(result.earliestAlarmAt)
        } else { Triple(-1, -1, -1) }

        updateState(
            uiState.value.copy(
                remainingTime = remainingTime,
                scheduleList = result.scheduleList
            )
        )

        processAlarms(result.scheduleList)
    }

    private fun processAlarms(schedules: List<Schedule>) {
        viewModelScope.launch {
            // 알람 리스트 추출
            val alarmRingInfos = schedules.flatMap { schedule ->
                buildList {
                    if (schedule.preparationAlarm.enabled) {
                        add(
                            AlarmRingInfo(
                                alarmDetail = schedule.preparationAlarm,
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
                            alarmDetail = schedule.departureAlarm,
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

            // 저장소에 저장
            alarmStorage.saveAlarms(alarmRingInfos)

            // 스케줄러 예약
            alarmRingInfos.forEach { info ->
                alarmScheduler.scheduleAlarm(info.alarmDetail, info.alarmType)
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
                    }
                )
            }
        }
    }
}