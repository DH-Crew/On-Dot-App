package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.UiState

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // Step1
    val preparationTime: Int = 0,
    val hourInput: String = "",
    val minuteInput: String = "",
    // Step2
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
): UiState
