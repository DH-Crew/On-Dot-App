package com.dh.ondot.presentation.alarm.preparation

import com.dh.ondot.core.ui.base.Event

sealed class PreparationAlarmRingEvent: Event {
    data object NavigateToSplash: PreparationAlarmRingEvent()
}