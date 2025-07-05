package com.dh.ondot.presentation.onboarding

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.response.AddressInfo

data class OnboardingUiState(
    val currentStep: Int = 0,
    val totalStep: Int = 0,

    // Step1
    val preparationTime: Int = 0,
    val hourInput: String = "",
    val minuteInput: String = "",
    // Step2
    val addressInput: String = "",
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val addressList: List<AddressInfo> = emptyList(),
    val selectedAddress: AddressInfo? = null

): UiState
