package com.ondot.alarm

import com.ondot.ui.base.Event

sealed class AlarmEvent: Event {
    data object NavigateToHome: AlarmEvent()
}