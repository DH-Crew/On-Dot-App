package com.ondot.general.contract

import com.ondot.domain.model.enums.ToastType
import com.ondot.ui.base.mvi.SideEffect

sealed interface GeneralScheduleSideEffect : SideEffect {
    data class ShowToast(
        val message: String,
        val type: ToastType,
    ) : GeneralScheduleSideEffect

    data object NavigateToPlacePicker : GeneralScheduleSideEffect

    data object NavigateToRouteLoading : GeneralScheduleSideEffect

    data object NavigateToMain : GeneralScheduleSideEffect

    data object RequestArrivalFocus : GeneralScheduleSideEffect
}
