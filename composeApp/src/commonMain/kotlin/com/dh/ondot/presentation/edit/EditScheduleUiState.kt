package com.dh.ondot.presentation.edit

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.ScheduleDetailResponse

data class EditScheduleUiState(
    val isInitialized: Boolean = false,
    val schedule: ScheduleDetailResponse = ScheduleDetailResponse()
) : UiState
