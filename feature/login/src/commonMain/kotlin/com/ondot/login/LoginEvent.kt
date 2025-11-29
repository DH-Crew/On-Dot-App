package com.ondot.login

import com.ondot.ui.base.Event

sealed class LoginEvent: Event {
    data object NavigateToOnboarding: LoginEvent()
    data object NavigateToMain: LoginEvent()
}