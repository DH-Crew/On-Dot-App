package com.dh.ondot.presentation.edit

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.ScheduleDetail
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EditScheduleUiState(
    val scheduleId: Long = -1,
    val isInitialized: Boolean = false,
    val schedule: ScheduleDetail = ScheduleDetail(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time,
    val showDeleteDialog: Boolean = false,
    val showDateBottomSheet: Boolean = false,
    val showTimeBottomSheet: Boolean = false
) : UiState
