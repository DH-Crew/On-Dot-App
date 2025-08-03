package com.dh.ondot.presentation.setting

import com.dh.ondot.core.ui.base.Event

sealed class SettingEvent: Event {
    data object NavigateToLoginScreen: SettingEvent()
}