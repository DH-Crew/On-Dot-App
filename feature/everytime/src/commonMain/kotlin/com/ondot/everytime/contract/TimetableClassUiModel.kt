package com.ondot.everytime.contract

import androidx.compose.runtime.Immutable
import com.ondot.domain.model.enums.DayOfWeekKey
import kotlinx.datetime.LocalTime

@Immutable
data class TimetableClassUiModel(
    val id: String,
    val day: DayOfWeekKey,
    val courseName: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isSelected: Boolean,
)
