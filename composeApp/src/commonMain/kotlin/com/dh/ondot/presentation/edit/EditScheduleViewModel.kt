package com.dh.ondot.presentation.edit

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.ScheduleDetailResponse
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_DETAIL
import kotlinx.coroutines.launch

class EditScheduleViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository
) : BaseViewModel<EditScheduleUiState>(EditScheduleUiState()) {

    private val logger = Logger.withTag("EditScheduleViewModel")

    fun getScheduleDetail(scheduleId: Long) {
        viewModelScope.launch {
            scheduleRepository.getScheduleDetail(scheduleId).collect {
                resultResponse(it, ::onSuccessGetScheduleDetail, ::onFailGetScheduleDetail)
            }
        }
    }

    private fun onSuccessGetScheduleDetail(result: ScheduleDetailResponse) {
        updateState(uiState.value.copy(schedule = result, isInitialized = true))
    }

    private fun onFailGetScheduleDetail(e: Throwable) {
        logger.e { "일정 상세 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_DETAIL, ToastType.ERROR) }
    }
}