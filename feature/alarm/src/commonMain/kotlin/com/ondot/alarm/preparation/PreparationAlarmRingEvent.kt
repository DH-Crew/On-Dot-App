package com.ondot.alarm.preparation

import com.ondot.ui.base.Event

sealed class PreparationAlarmRingEvent: Event {
    data object NavigateToSplash: PreparationAlarmRingEvent()
}