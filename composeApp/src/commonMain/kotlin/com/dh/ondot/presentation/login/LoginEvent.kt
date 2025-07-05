package com.dh.ondot.presentation.login

import com.dh.ondot.core.ui.base.Event

sealed class LoginEvent: Event {
    data object NavigateToOnboarding: LoginEvent()
}