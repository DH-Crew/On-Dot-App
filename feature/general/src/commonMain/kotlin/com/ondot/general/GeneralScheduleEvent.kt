package com.ondot.general

import com.ondot.ui.base.Event

sealed class GeneralScheduleEvent: Event {
    data object NavigateToPlacePicker: GeneralScheduleEvent()
    data object NavigateToRouteLoading: GeneralScheduleEvent()
    data object NavigateToMain: GeneralScheduleEvent()
    data object RequestArrivalFocus: GeneralScheduleEvent()
}