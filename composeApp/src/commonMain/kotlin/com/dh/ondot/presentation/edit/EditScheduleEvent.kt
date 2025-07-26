package com.dh.ondot.presentation.edit

import com.dh.ondot.core.ui.base.Event

sealed class EditScheduleEvent: Event {
    data object NavigateToHomeScreen: EditScheduleEvent()
}