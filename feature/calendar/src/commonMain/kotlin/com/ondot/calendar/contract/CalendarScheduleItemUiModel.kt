package com.ondot.calendar.contract

import androidx.compose.runtime.Immutable

@Immutable
data class CalendarScheduleItemUiModel(
    val scheduleId: Long,
    val title: String,
    val appointmentTimeText: String,
    val alarmInfoText: String,
    val repeatText: String?,
    val isRepeat: Boolean,
    val isAlarmEnabled: Boolean,
    val isPast: Boolean,
)
