package com.ondot.onboarding.contract

import com.ondot.domain.model.enums.ToastType
import com.ondot.ui.base.mvi.SideEffect

sealed interface OnboardingSideEffect : SideEffect {
    data class ShowToast(
        val message: String,
        val type: ToastType,
    ) : OnboardingSideEffect

    object NavigateToMainScreen : OnboardingSideEffect

    object NavigateToEverytime : OnboardingSideEffect

    object NavigateToGeneralSchedule : OnboardingSideEffect
}
