package com.dh.ondot.presentation.app

import com.dh.ondot.core.ui.base.Event

sealed class AppEvent: Event {
    data object NavigateToSplash: AppEvent()
}