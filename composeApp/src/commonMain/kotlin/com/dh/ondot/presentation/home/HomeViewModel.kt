package com.dh.ondot.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.domain.model.response.ScheduleListResponse
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository,
    private val alarmStorage: AlarmStorage = ServiceLocator.provideAlarmStorage(),
    private val alarmScheduler: AlarmScheduler = ServiceLocator.provideAlarmScheduler()
) : BaseViewModel<HomeUiState>(HomeUiState()) {
    private val logger = Logger.withTag("HomeViewModel")

    fun onToggle() {
        updateState(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(id: Long, isEnabled: Boolean) {
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.map {
            if (it.scheduleId == id) {
                it.copy(isEnabled = isEnabled)
            } else {
                it
            }
        }))
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
            val alarmsWithType = schedules.flatMap { schedule ->
                buildList {
                    if (schedule.preparationAlarm.enabled) {
                        add(schedule.preparationAlarm to AlarmType.Preparation)
                    }
                    add(schedule.departureAlarm to AlarmType.Departure)
                }
            }

            // 저장소에 저장
            alarmStorage.saveAlarms(alarmsWithType.map { it.first })

            // 스케줄러 예약
            alarmsWithType.forEach { (alarm, type) ->
                alarmScheduler.scheduleAlarm(alarm, type)
            }
        }
    }

    private fun onFailureGetScheduleList(throwable: Throwable) {
        logger.e { throwable.message.toString() }

        viewModelScope.launch { ToastManager.show(message = ERROR_GET_SCHEDULE_LIST, type = ToastType.ERROR) }
    }
}