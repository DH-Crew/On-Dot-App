package com.ondot.calendar.contract

import com.ondot.domain.model.enums.ToastType
import com.ondot.ui.base.mvi.SideEffect

sealed interface CalendarSideEffect : SideEffect {
    data class ShowToast(
        val message: String,
        val type: ToastType,
    ) : CalendarSideEffect
}
