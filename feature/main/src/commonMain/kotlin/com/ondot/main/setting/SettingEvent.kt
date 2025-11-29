package com.ondot.main.setting

import com.ondot.ui.base.Event

sealed class SettingEvent: Event {
    data object NavigateToLoginScreen: SettingEvent()
    data object PopScreen: SettingEvent()
}