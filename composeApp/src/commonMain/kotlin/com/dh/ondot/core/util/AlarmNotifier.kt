package com.dh.ondot.core.util

import com.dh.ondot.domain.model.ui.AlarmEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AlarmNotifier {
    private val _events = MutableSharedFlow<AlarmEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AlarmEvent> = _events.asSharedFlow()

    fun notify(event: AlarmEvent) {
        _events.tryEmit(event)
    }
}