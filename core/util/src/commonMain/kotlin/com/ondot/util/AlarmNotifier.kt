package com.ondot.util

import com.ondot.domain.model.ui.AlarmEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy

object AlarmNotifier {
    private val _events = MutableSharedFlow<AlarmEvent>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AlarmEvent> = _events.asSharedFlow()

    fun notify(event: AlarmEvent) {
        println("[AlarmNotifier] tryEmit $event")
        val ok = _events.tryEmit(event)
    }

    /**
     * 최초 1회 내비게이션만 일으키도록, 마지막으로 처리한 이벤트는 걸러줌
     * */
    fun flow(): Flow<AlarmEvent> =
        events.distinctUntilChangedBy { it.key }
}