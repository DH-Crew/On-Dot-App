package com.dh.ondot.presentation.home

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.Schedule

data class HomeUiState(
    val remainingTime: Triple<Int, Int, Int> = Triple(-1, -1, -1),
    val isExpanded: Boolean = false,
    val scheduleList: List<Schedule> = emptyList()
): UiState
