package com.ondot.edit

import com.ondot.ui.base.Event

sealed class EditScheduleEvent: Event {
    data object NavigateToHomeScreen: EditScheduleEvent()
}