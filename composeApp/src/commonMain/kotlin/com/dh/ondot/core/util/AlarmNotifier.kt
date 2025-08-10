package com.dh.ondot.core.util

import com.dh.ondot.domain.model.ui.AlarmEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AlarmNotifier {
    private val _events = MutableSharedFlow<AlarmEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AlarmEvent> = _events.asSharedFlow()

    fun notify(event: AlarmEvent) {
        println("[AlarmNotifier] tryEmit $event")
        val ok = _events.tryEmit(event)
        println("[AlarmNotifier] emitted=$ok replay=${(events as? MutableSharedFlow<AlarmEvent>)?.replayCache?.size}")
    }
}