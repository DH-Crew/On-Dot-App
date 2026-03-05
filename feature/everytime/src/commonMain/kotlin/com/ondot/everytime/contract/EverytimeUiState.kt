package com.ondot.everytime.contract

import androidx.compose.runtime.Immutable
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.ui.base.UiState

@Immutable
data class EverytimeUiState(
    val timetable: EverytimeValidateTimetable? = null,
) : UiState
