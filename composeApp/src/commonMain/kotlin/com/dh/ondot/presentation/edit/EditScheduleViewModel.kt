package com.dh.ondot.presentation.edit

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.toLocalDateFromIso
import com.dh.ondot.core.util.DateTimeFormatter.toLocalTimeFromIso
import com.dh.ondot.domain.model.enums.ToastType
import com.dh.ondot.domain.model.response.ScheduleDetail
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_EDIT_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_DETAIL
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EditScheduleViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository
) : BaseViewModel<EditScheduleUiState>(EditScheduleUiState()) {

    private val logger = Logger.withTag("EditScheduleViewModel")

    /**------------------------------------------일정 상세 조회-------------------------------------------------*/

    fun getScheduleDetail(scheduleId: Long) {
        viewModelScope.launch {
            scheduleRepository.getScheduleDetail(scheduleId).collect {
                resultResponse(it, ::onSuccessGetScheduleDetail, ::onFailGetScheduleDetail)
            }
        }
    }

    private fun onSuccessGetScheduleDetail(result: ScheduleDetail) {
        val date = if (result.repeatDays.isEmpty()) result.appointmentAt.toLocalDateFromIso() else null
        val time = result.appointmentAt.toLocalTimeFromIso()

        updateState(
            uiState.value.copy(
                schedule = result.copy(repeatDays = result.repeatDays.map { it - 1 }),
                isInitialized = true,
                selectedDate = date,
                selectedTime = time,
            )
        )
    }

    private fun onFailGetScheduleDetail(e: Throwable) {
        logger.e { "일정 상세 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_DETAIL, ToastType.ERROR) }
    }

    /**------------------------------------------일정 수정-------------------------------------------------*/

    fun saveSchedule() {
        val newDate = DateTimeFormatter.formatIsoDateTime(
            date = uiState.value.selectedDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            time = uiState.value.selectedTime
        )
        val newSchedule = uiState.value.schedule.copy(
            appointmentAt = newDate,
            repeatDays = uiState.value.schedule.repeatDays.map { it + 1 }
        )

        viewModelScope.launch {
            scheduleRepository.editSchedule(uiState.value.scheduleId , newSchedule).collect {
                resultResponse(it, ::onSuccessSaveSchedule, ::onFailSaveSchedule)
            }
        }
    }

    private fun onSuccessSaveSchedule(result: Unit) {
        emitEventFlow(EditScheduleEvent.NavigateToHomeScreen)
    }

    private fun onFailSaveSchedule(e: Throwable) {
        logger.e { "일정 수정 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_EDIT_SCHEDULE, ToastType.ERROR) }
    }

    /**------------------------------------------일정 삭제-------------------------------------------------*/

    fun deleteSchedule() {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(uiState.value.scheduleId).collect {
                resultResponse(it, ::onSuccessDeleteSchedule, ::onFailDeleteSchedule)
            }
        }
    }

    private fun onSuccessDeleteSchedule(result: Unit) {
        emitEventFlow(EditScheduleEvent.NavigateToHomeScreen)
    }

    private fun onFailDeleteSchedule(e: Throwable) {
        logger.e { "일정 삭제 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR) }
    }

    /**------------------------------------------상태 변수 처리-------------------------------------------------*/

    fun updateScheduleId(scheduleId: Long) { updateState(uiState.value.copy(scheduleId = scheduleId)) }

    fun updateScheduleTitle(title: String) {
        updateState(uiState.value.copy(schedule = uiState.value.schedule.copy(title = title)))
    }

    fun toggleSwitch() {
        val current = uiState.value
        val prep = current.schedule.preparationAlarm
        updateState(
            current.copy(
                schedule = current.schedule.copy(
                    preparationAlarm = prep.copy(enabled = !prep.enabled)
                )
            )
        )
    }

    fun showDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = true))
    }

    fun hideDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = false))
    }

    fun editDate(isRepeat: Boolean, repeatDays: Set<Int>, date: LocalDate?) {
        logger.d { "isRepeat: $isRepeat, repeatDays: $repeatDays, date: $date" }

        if (isRepeat && repeatDays.isEmpty()) return

        updateState(
            uiState.value.copy(
                schedule = uiState.value.schedule.copy(
                    isRepeat = isRepeat,
                    repeatDays = repeatDays.toList()
                ),
                selectedDate = if (isRepeat) null else date
            )
        )
    }

    fun showDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = true))
    }

    fun hideDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = false))
    }

    fun showTimeBottomSheet() {
        updateState(uiState.value.copy(showTimeBottomSheet = true))
    }

    fun hideTimeBottomSheet() {
        updateState(uiState.value.copy(showTimeBottomSheet = false))
    }
}