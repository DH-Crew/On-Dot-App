package com.dh.ondot.presentation.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.ScheduleListResponse
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository
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
        val remainingTime = DateTimeFormatter.calculateRemainingTime(result.earliestAlarmAt)

        updateState(
            uiState.value.copy(
                remainingTime = remainingTime,
                scheduleList = result.scheduleList
            )
        )
    }

    private fun onFailureGetScheduleList(throwable: Throwable) {
        logger.e { throwable.message.toString() }

        viewModelScope.launch { ToastManager.show(message = ERROR_GET_SCHEDULE_LIST, type = ToastType.ERROR) }
    }
}