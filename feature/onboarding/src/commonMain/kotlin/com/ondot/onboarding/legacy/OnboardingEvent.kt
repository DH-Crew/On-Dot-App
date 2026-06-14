package com.ondot.onboarding.legacy

import com.ondot.ui.base.Event

sealed class OnboardingEvent : Event {
    data object NavigateToMainScreen : OnboardingEvent()
}
