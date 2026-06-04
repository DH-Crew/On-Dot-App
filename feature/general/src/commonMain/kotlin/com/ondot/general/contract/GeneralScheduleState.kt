package com.ondot.general.contract

import androidx.compose.runtime.Immutable
import com.dh.ondot.presentation.ui.theme.NEW_SCHEDULE_LABEL
import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.enums.TransportType
import com.ondot.general.GeneralScheduleUiState
import com.ondot.ui.base.UiState
import com.ondot.ui.screen.placepicker.model.PlacePickerUiModel
import com.ondot.util.DateTimeFormatter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
private fun today(): LocalDate =
    Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

@Immutable
data class GeneralScheduleState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null,
    val activeWeekDays: Set<Int> = emptySet(),
    val isActiveCalendar: Boolean = true,
    val isActiveDial: Boolean = false,
    val calendarMonth: LocalDate = today().let { LocalDate(it.year, it.month, 1) },
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val today: LocalDate = today(),
    val isInitialPlacePicker: Boolean = true,
    val isHomeAddressInitialized: Boolean = false,
    val placePickerState: PlacePickerUiModel = PlacePickerUiModel(),
    val preparationAlarm: Alarm = Alarm(),
    val departureAlarm: Alarm = Alarm(),
    val scheduleTitle: String = NEW_SCHEDULE_LABEL,
    val showBottomSheet: Boolean = false,
) : UiState {
    val isRepeatStepButtonEnabled: Boolean
        get() = selectedTime != null && (selectedDate != null || activeWeekDays.isNotEmpty())

    val isPlacePickerButtonEnabled: Boolean
        get() = placePickerState.selectedDeparturePlace != null && placePickerState.selectedArrivalPlace != null

    companion object {
        fun formattedDate(date: String) = DateTimeFormatter.formatKoreanDateMonthDay(date)
    }
}

fun GeneralScheduleState.toLegacyUiState(): GeneralScheduleUiState =
    GeneralScheduleUiState(
        currentStep = currentStep,
        totalStep = totalStep,
        isRepeat = isRepeat,
        activeCheckChip = activeCheckChip,
        activeWeekDays = activeWeekDays,
        isActiveCalendar = isActiveCalendar,
        isActiveDial = isActiveDial,
        calendarMonth = calendarMonth,
        selectedDate = selectedDate,
        selectedTime = selectedTime,
        today = today,
        isInitialPlacePicker = isInitialPlacePicker,
        isHomeAddressInitialized = isHomeAddressInitialized,
        placePickerState = placePickerState,
        preparationAlarm = preparationAlarm,
        departureAlarm = departureAlarm,
        scheduleTitle = scheduleTitle,
        showBottomSheet = showBottomSheet,
    )
