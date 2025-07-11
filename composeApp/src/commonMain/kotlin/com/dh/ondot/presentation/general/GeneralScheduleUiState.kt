package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.enums.RouterType
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.presentation.ui.theme.NEW_SCHEDULE_LABEL
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class GeneralScheduleUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // ScheduleRepeat
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null, // 0: 매일, 1: 주중, 2: 주말
    val activeWeekDays: Set<Int> = emptySet(),

    // ScheduleDate
    val isActiveCalendar: Boolean = false,
    val isActiveDial: Boolean = false,
    val calendarMonth: LocalDate = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .let { today ->
            LocalDate(today.year, today.monthNumber, 1)
        },
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,

    // PlacePicker
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
    val preparationAlarm: AlarmDetail = AlarmDetail(),
    val departureAlarm: AlarmDetail = AlarmDetail(),

    // CheckSchedule
    val scheduleTitle: String = NEW_SCHEDULE_LABEL,

): UiState
