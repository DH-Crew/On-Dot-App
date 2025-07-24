package com.dh.ondot.presentation.app

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.ui.AlarmRingInfo

data class AppUiState(
    val alarmRingInfo: AlarmRingInfo = AlarmRingInfo(),
    val showPreparationStartAnimation: Boolean = false,
): UiState
