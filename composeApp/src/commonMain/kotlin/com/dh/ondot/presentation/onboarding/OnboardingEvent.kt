package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.Event

sealed class OnboardingEvent: Event {
    data object NavigateToMainScreen: OnboardingEvent()
}