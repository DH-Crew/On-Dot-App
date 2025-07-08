package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.UiState

data class GeneralScheduleUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // ScheduleRepeat
    val isRepeat: Boolean = false,
    val activeCheckChip: Int? = null, // 0: 매일, 1: 주중, 2: 주말
    val activeWeekDays: Set<Int> = emptySet(),

    // ScheduleDate
    val isActiveCalendar: Boolean = false,

): UiState
