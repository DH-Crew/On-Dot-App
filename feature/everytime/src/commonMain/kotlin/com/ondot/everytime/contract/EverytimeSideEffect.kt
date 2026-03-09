package com.ondot.everytime.contract

import com.ondot.domain.model.enums.ToastType
import com.ondot.ui.base.mvi.SideEffect

sealed interface EverytimeSideEffect : SideEffect {
    data class ShowToast(
        val message: String,
        val toastType: ToastType,
    ) : EverytimeSideEffect

    data object NavigateToTimetable : EverytimeSideEffect

    data object NavigateToNext : EverytimeSideEffect
}
