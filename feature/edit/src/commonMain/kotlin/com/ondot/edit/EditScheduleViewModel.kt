package com.ondot.edit

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.ERROR_DELETE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_EDIT_SCHEDULE
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_DETAIL
import com.dh.ondot.presentation.ui.theme.ERROR_UPDATE_ALARM
import com.ondot.domain.model.enums.TimeBottomSheet
import com.ondot.domain.model.enums.TimeType
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.ScheduleAlarmManager
import com.ondot.ui.base.BaseViewModel
import com.ondot.ui.util.ToastManager
import com.ondot.util.DateTimeFormatter
import com.ondot.util.DateTimeFormatter.toIsoTimeString
import com.ondot.util.DateTimeFormatter.toLocalDateFromIso
import com.ondot.util.DateTimeFormatter.toLocalTimeFromIso
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EditScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleAlarmManager: ScheduleAlarmManager,
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
                originalSchedule = result,
                isInitialized = true,
                selectedDate = date,
                selectedTime = time,
            ),
        )
    }

    private fun onFailGetScheduleDetail(e: Throwable) {
        logger.e { "일정 상세 조회 실패: ${e.message}" }
        viewModelScope.launch { ToastManager.show(ERROR_GET_SCHEDULE_DETAIL, ToastType.ERROR) }
    }

    /**------------------------------------------일정 수정-------------------------------------------------*/

    @OptIn(ExperimentalTime::class)
    fun saveSchedule() {
        val originalScheduleDetail = uiState.value.originalSchedule ?: return

        val zone = TimeZone.currentSystemDefault()
        val today =
            Clock.System
                .now()
                .toLocalDateTime(zone)
                .date
        val selectedDate = uiState.value.selectedDate ?: today

        val newAppointmentAt =
            DateTimeFormatter.formatIsoDateTime(
                date = selectedDate,
                time =
                    uiState.value.schedule.appointmentAt
                        .toLocalTimeFromIso(),
            )

        val updatedScheduleDetail =
            uiState.value.schedule.copy(
                appointmentAt = newAppointmentAt,
                repeatDays =
                    uiState.value.schedule.repeatDays
                        .map { it + 1 },
            )

        val scheduleId = uiState.value.scheduleId
        val originalSchedule = originalScheduleDetail.toSchedule(scheduleId)
        val updatedSchedule = updatedScheduleDetail.toSchedule(scheduleId)

        viewModelScope.launch {
            val replaceResult = scheduleAlarmManager.replace(originalSchedule, updatedSchedule)
            if (replaceResult.isFailure) {
                logger.e(replaceResult.exceptionOrNull()) { "알람 재스케줄링 실패" }
                ToastManager.show(ERROR_UPDATE_ALARM, ToastType.ERROR)
                return@launch
            }

            var remoteError: Throwable? = null
            scheduleRepository.editSchedule(scheduleId, updatedScheduleDetail).collect { result ->
                result.onFailure { remoteError = it }
            }

            if (remoteError == null) {
                scheduleRepository.upsertLocalSchedule(updatedSchedule)
                emitEventFlow(EditScheduleEvent.NavigateBack)
            } else {
                logger.e(remoteError) { "일정 수정 실패, 알람 롤백 시도" }

                val rollbackResult = scheduleAlarmManager.replace(updatedSchedule, originalSchedule)
                rollbackResult.onFailure { rollbackError ->
                    logger.e(rollbackError) { "알람 롤백 실패" }
                }

                ToastManager.show(ERROR_EDIT_SCHEDULE, ToastType.ERROR)
            }
        }
    }

    /**------------------------------------------일정 삭제-------------------------------------------------*/

    fun deleteSchedule() {
        val targetSchedule = uiState.value.schedule.toSchedule(uiState.value.scheduleId)

        viewModelScope.launch {
            try {
                if (targetSchedule.hasActiveAlarm) {
                    scheduleAlarmManager.cancel(targetSchedule)
                }

                var remoteError: Throwable? = null
                scheduleRepository.deleteSchedule(uiState.value.scheduleId).collect { result ->
                    result.onFailure { remoteError = it }
                }

                if (remoteError == null) {
                    emitEventFlow(EditScheduleEvent.NavigateBack)
                } else {
                    logger.e(remoteError) { "일정 삭제 실패, 알람 복구 시도" }

                    runCatching {
                        if (targetSchedule.hasActiveAlarm) {
                            scheduleAlarmManager.schedule(targetSchedule)
                        }
                    }.onFailure { rollbackError ->
                        logger.e(rollbackError) { "삭제 실패 후 알람 복구 실패" }
                    }

                    ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
                }
            } catch (t: Throwable) {
                logger.e(t) { "일정 삭제 전 알람 취소 실패" }
                ToastManager.show(ERROR_DELETE_SCHEDULE, ToastType.ERROR)
            }
        }
    }

    /**------------------------------------------상태 변수 처리-------------------------------------------------*/

    fun updateScheduleId(scheduleId: Long) {
        updateState(uiState.value.copy(scheduleId = scheduleId))
    }

    fun updateScheduleTitle(title: String) {
        updateState(uiState.value.copy(schedule = uiState.value.schedule.copy(title = title)))
    }

    fun toggleSwitch() {
        val current = uiState.value
        val prep = current.schedule.preparationAlarm
        updateState(
            current.copy(
                schedule =
                    current.schedule.copy(
                        preparationAlarm = prep.copy(enabled = !prep.enabled),
                    ),
            ),
        )
    }

    fun showDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = true))
    }

    fun hideDeleteDialog() {
        updateState(uiState.value.copy(showDeleteDialog = false))
    }

    fun editDate(
        isRepeat: Boolean,
        repeatDays: Set<Int>,
        date: LocalDate?,
    ) {
        logger.d { "isRepeat: $isRepeat, repeatDays: $repeatDays, date: $date" }

        if (isRepeat && repeatDays.isEmpty()) return

        updateState(
            uiState.value.copy(
                schedule =
                    uiState.value.schedule.copy(
                        isRepeat = isRepeat,
                        repeatDays = repeatDays.toList(),
                    ),
                selectedDate = if (isRepeat) null else date,
            ),
        )
    }

    fun showDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = true))
    }

    fun hideDateBottomSheet() {
        updateState(uiState.value.copy(showDateBottomSheet = false))
    }

    fun editTime(
        newDate: LocalDate,
        newTime: LocalTime,
    ) {
        when (uiState.value.selectedTimeType) {
            TimeType.APPOINTMENT ->
                updateState(
                    uiState.value.copy(
                        schedule = uiState.value.schedule.copy(appointmentAt = newTime.toIsoTimeString()),
                    ),
                )
            TimeType.DEPARTURE ->
                updateState(
                    uiState.value.copy(
                        schedule =
                            uiState.value.schedule.copy(
                                departureAlarm =
                                    uiState.value.schedule.departureAlarm.copy(
                                        triggeredAt =
                                            DateTimeFormatter.formatIsoDateTime(
                                                date = newDate,
                                                time = newTime,
                                            ),
                                    ),
                            ),
                    ),
                )
            TimeType.PREPARATION ->
                updateState(
                    uiState.value.copy(
                        schedule =
                            uiState.value.schedule.copy(
                                preparationAlarm =
                                    uiState.value.schedule.preparationAlarm.copy(
                                        triggeredAt =
                                            DateTimeFormatter.formatIsoDateTime(
                                                date = newDate,
                                                time = newTime,
                                            ),
                                    ),
                            ),
                    ),
                )
        }
    }

    fun showTimeBottomSheet(timeType: TimeType) {
        val selectedTime =
            when (timeType) {
                TimeType.APPOINTMENT ->
                    uiState.value.schedule.appointmentAt
                        .toLocalTimeFromIso()
                TimeType.DEPARTURE ->
                    uiState.value.schedule.departureAlarm.triggeredAt
                        .toLocalTimeFromIso()
                TimeType.PREPARATION ->
                    uiState.value.schedule.preparationAlarm.triggeredAt
                        .toLocalTimeFromIso()
            }
        val selectedAlarmDate =
            when (timeType) {
                TimeType.DEPARTURE ->
                    runCatching {
                        uiState.value.schedule.departureAlarm.triggeredAt
                            .toLocalDateFromIso()
                    }.getOrNull()
                TimeType.PREPARATION ->
                    runCatching {
                        uiState.value.schedule.preparationAlarm.triggeredAt
                            .toLocalDateFromIso()
                    }.getOrNull()
                else -> null
            }
        val bottomSheetType =
            when (timeType) {
                TimeType.APPOINTMENT -> TimeBottomSheet.Schedule
                TimeType.DEPARTURE -> TimeBottomSheet.Alarm
                TimeType.PREPARATION -> TimeBottomSheet.Alarm
            }

        updateStateSync(
            uiState.value.copy(
                activeTimeBottomSheet = bottomSheetType,
                selectedTimeType = timeType,
                selectedTime = selectedTime,
                selectedAlarmDate = selectedAlarmDate,
            ),
        )
    }

    fun hideTimeBottomSheet() {
        updateState(uiState.value.copy(activeTimeBottomSheet = null))
    }

    private fun ScheduleDetail.toSchedule(scheduleId: Long): Schedule =
        Schedule(
            scheduleId = scheduleId,
            scheduleTitle = title,
            isRepeat = isRepeat,
            repeatDays = repeatDays,
            appointmentAt = appointmentAt,
            departureAlarm = departureAlarm,
            preparationAlarm = preparationAlarm,
            startLatitude = departurePlace.latitude,
            startLongitude = departurePlace.longitude,
            endLatitude = arrivalPlace.latitude,
            endLongitude = arrivalPlace.longitude,
            hasActiveAlarm = departureAlarm.enabled || preparationAlarm.enabled,
        )
}
