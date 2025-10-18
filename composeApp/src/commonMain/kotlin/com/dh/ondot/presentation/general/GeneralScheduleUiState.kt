package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.presentation.ui.theme.NEW_SCHEDULE_LABEL
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.alarm.Alarm
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class GeneralScheduleUiState @OptIn(ExperimentalTime::class) constructor(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // ScheduleRepeat
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null, // 0: 매일, 1: 주중, 2: 주말
    val activeWeekDays: Set<Int> = emptySet(),

    // ScheduleDate
    val isActiveCalendar: Boolean = true,
    val isActiveDial: Boolean = false,
    val calendarMonth: LocalDate = kotlin.time.Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .let { today ->
            LocalDate(today.year, today.monthNumber, 1)
        },
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    // PlacePicker
    val isInitialPlacePicker: Boolean = true,
    val isChecked: Boolean = false,
    val placeList: List<AddressInfo> = emptyList(),
    val lastFocusedTextField: RouterType = RouterType.Departure,
    val departurePlaceInput: String = "",
    val arrivalPlaceInput: String = "",
    val selectedDeparturePlace: AddressInfo? = null,
    val selectedArrivalPlace: AddressInfo? = null,
    val homeAddress: AddressInfo = AddressInfo(),
    val isHomeAddressInitialized: Boolean = false,

    // RouteLoading
    val preparationAlarm: Alarm = Alarm(),
    val departureAlarm: Alarm = Alarm(),

    // CheckSchedule
    val scheduleTitle: String = NEW_SCHEDULE_LABEL,
    val showBottomSheet: Boolean = false,

    ): UiState
