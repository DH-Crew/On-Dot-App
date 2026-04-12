package com.ondot.calendar.contract

import com.ondot.ui.base.mvi.Intent
import kotlinx.datetime.LocalDate

sealed interface CalendarIntent : Intent {
    data class SelectDate(
        val date: LocalDate,
    ) : CalendarIntent

    data object MoveToPreviousMonth : CalendarIntent

    data object MoveToNextMonth : CalendarIntent

    data class ToggleAlarm(
        val scheduleId: Long,
        val enabled: Boolean,
    ) : CalendarIntent

    object Init : CalendarIntent

    data class DeleteHistory(
        val scheduleId: Long,
        val isPast: Boolean,
    ) : CalendarIntent
}
