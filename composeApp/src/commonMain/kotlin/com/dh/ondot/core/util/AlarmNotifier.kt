package com.dh.ondot.core.util

import com.dh.ondot.domain.model.ui.AlarmEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AlarmNotifier {
    private val _events = MutableSharedFlow<AlarmEvent>(
        replay = 1, // 늦게 구독한 쪽에도 전달, ios에서 화면 전환이 안돼서 추가한 코드
        extraBufferCapacity = 1
    )
    val events: SharedFlow<AlarmEvent> = _events.asSharedFlow()

    fun notify(event: AlarmEvent) {
        println("[AlarmNotifier] tryEmit $event")
        val ok = _events.tryEmit(event)
        println("[AlarmNotifier] emitted=$ok replay=${(events as? MutableSharedFlow<AlarmEvent>)?.replayCache?.size}")
    }
}