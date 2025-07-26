package com.dh.ondot.presentation.edit.bottomSheet

import com.dh.ondot.core.ui.base.UiState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EditBottomSheetUiState(
    /**
     * EditDateBottomSheet
     * */
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null,
    val repeatDays: Set<Int> = emptySet(),
    val currentDate: LocalDate? = null,
    val calendarMonth: LocalDate = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .let { today ->
            LocalDate(today.year, today.monthNumber, 1)
        },

    /**
     * EditTimeBottomSheet
     * */


): UiState
