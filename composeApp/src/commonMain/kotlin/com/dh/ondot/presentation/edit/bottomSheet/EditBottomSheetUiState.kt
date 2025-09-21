package com.dh.ondot.presentation.edit.bottomSheet

import com.dh.ondot.core.ui.base.UiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class EditBottomSheetUiState @OptIn(ExperimentalTime::class) constructor(
    /**
     * EditDateBottomSheet
     * */
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null,
    val repeatDays: Set<Int> = emptySet(),
    val currentDate: LocalDate? = null,
    val calendarMonth: LocalDate = kotlin.time.Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .let { today ->
            LocalDate(today.year, today.monthNumber, 1)
        },

    /**
     * EditTimeBottomSheet
     * */
    val currentTime: LocalTime? = null,
    val isActiveCalendar: Boolean = false,

): UiState
