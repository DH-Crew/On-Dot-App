package com.dh.ondot.presentation.edit

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.ScheduleDetail

data class EditScheduleUiState(
    val scheduleId: Long = -1,
    val isInitialized: Boolean = false,
    val schedule: ScheduleDetail = ScheduleDetail(),
    val showDeleteDialog: Boolean = false
) : UiState
