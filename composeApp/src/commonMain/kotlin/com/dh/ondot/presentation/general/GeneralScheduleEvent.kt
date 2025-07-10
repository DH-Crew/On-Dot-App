package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.Event

sealed class GeneralScheduleEvent: Event {
    data object NavigateToPlacePicker: GeneralScheduleEvent()
    data object NavigateToRouteLoading: GeneralScheduleEvent()
}