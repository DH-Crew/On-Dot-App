package com.dh.ondot.presentation.edit

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.core.ui.util.ToastManager
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.toIsoTimeString
import com.dh.ondot.core.util.DateTimeFormatter.toLocalDateFromIso
import com.dh.ondot.core.util.DateTimeFormatter.toLocalTimeFromIso
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_EDIT_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_DETAIL
import com.ondot.domain.model.enums.TimeBottomSheet
import com.ondot.domain.model.enums.TimeType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.response.ScheduleDetail
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.AlarmScheduler
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class EditScheduleViewModel(
    private val scheduleRepository: ScheduleRepository = ServiceLocator.scheduleRepository,
    private val alarmScheduler: AlarmScheduler = ServiceLocator.provideAlarmScheduler()
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

    @OptIn(ExperimentalTime::class)
    fun saveSchedule() {
        val newDate = DateTimeFormatter.formatIsoDateTime(
            date = uiState.value.selectedDate ?: kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            time = uiState.value.schedule.appointmentAt.toLocalTimeFromIso()
        )
        val newSchedule = uiState.value.schedule.copy(
            appointmentAt = newDate,
            repeatDays = uiState.value.schedule.repeatDays.map { it + 1 }
        )

        if (!newSchedule.preparationAlarm.enabled) cancelAlarm(newSchedule.preparationAlarm.alarmId)

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
        cancelAlarms()

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

    /**--------------------------------------------알람 취소-----------------------------------------------*/

    /**일정을 삭제할 때 스케줄링된 알람도 함께 삭제*/
    private fun cancelAlarms() {
        cancelAlarm(uiState.value.schedule.preparationAlarm.alarmId)
        cancelAlarm(uiState.value.schedule.departureAlarm.alarmId)
    }

    private fun cancelAlarm(alarmId: Long) {
        alarmScheduler.cancelAlarm(alarmId)
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

    fun editTime(newDate: LocalDate, newTime: LocalTime) {
        when(uiState.value.selectedTimeType) {
            TimeType.APPOINTMENT -> updateState(
                uiState.value.copy(
                    schedule = uiState.value.schedule.copy(appointmentAt = newTime.toIsoTimeString())
                )
            )
            TimeType.DEPARTURE -> updateState(
                uiState.value.copy(
                    schedule = uiState.value.schedule.copy(
                        departureAlarm = uiState.value.schedule.departureAlarm.copy(
                            triggeredAt = DateTimeFormatter.formatIsoDateTime(
                                date = newDate,
                                time = newTime
                            )
                        )
                    )
                )
            )
            TimeType.PREPARATION -> updateState(
                uiState.value.copy(
                    schedule = uiState.value.schedule.copy(
                        preparationAlarm = uiState.value.schedule.preparationAlarm.copy(
                            triggeredAt = DateTimeFormatter.formatIsoDateTime(
                                date = newDate,
                                time = newTime
                            )
                        )
                    )
                )
            )
        }
    }

    fun showTimeBottomSheet(timeType: TimeType) {
        val selectedTime = when(timeType) {
            TimeType.APPOINTMENT -> uiState.value.schedule.appointmentAt.toLocalTimeFromIso()
            TimeType.DEPARTURE -> uiState.value.schedule.departureAlarm.triggeredAt.toLocalTimeFromIso()
            TimeType.PREPARATION -> uiState.value.schedule.preparationAlarm.triggeredAt.toLocalTimeFromIso()
        }
        val selectedAlarmDate = when(timeType) {
            TimeType.DEPARTURE -> runCatching { uiState.value.schedule.departureAlarm.triggeredAt.toLocalDateFromIso() }.getOrNull()
            TimeType.PREPARATION -> runCatching { uiState.value.schedule.preparationAlarm.triggeredAt.toLocalDateFromIso() }.getOrNull()
            else -> null
        }
        val bottomSheetType = when(timeType) {
            TimeType.APPOINTMENT -> TimeBottomSheet.Schedule
            TimeType.DEPARTURE -> TimeBottomSheet.Alarm
            TimeType.PREPARATION -> TimeBottomSheet.Alarm
        }

        updateStateSync(
            uiState.value.copy(
                activeTimeBottomSheet = bottomSheetType,
                selectedTimeType = timeType,
                selectedTime = selectedTime,
                selectedAlarmDate = selectedAlarmDate
            )
        )
    }

    fun hideTimeBottomSheet() {
        updateState(uiState.value.copy(activeTimeBottomSheet = null))
    }
}