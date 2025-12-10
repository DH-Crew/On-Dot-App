package com.ondot.api

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


sealed interface AppNavEvent {
    data class OpenAlarm(val scheduleId: Long?, val alarmId: Long?) : AppNavEvent
    object OpenToday : AppNavEvent
    object OpenGeneralSchedule: AppNavEvent
}

object AppNavBus {
    private val _events = MutableSharedFlow<AppNavEvent>(
        replay = 0,
        extraBufferCapacity = 8
    )
    val events: SharedFlow<AppNavEvent> = _events

    fun emit(event: AppNavEvent) {
        _events.tryEmit(event)
    }
}

/**
 * URL 문자열을 AppNavEvent로 변환하는 역할
 */
fun interface DeepLinkParser {
    fun parse(url: String): AppNavEvent?
}

/**
 * 플랫폼이 URL을 던지면,
 * parser로 변환 후 bus에 emit 하는 역할
 */
interface DeepLinkDispatcher {
    fun dispatch(url: String): Boolean
}

/**
 * 앱에서 1회 초기화해서 parser를 주입
 */
object DeepLinkKit {
    private var parser: DeepLinkParser? = null

    fun install(parser: DeepLinkParser) {
        this.parser = parser
    }

    /**
     * 양 플랫폼에서 URL 문자열만 던지면 된다
     */
    fun handleUrl(url: String): Boolean {
        val p = parser ?: return false
        val event = p.parse(url) ?: return false
        AppNavBus.emit(event)
        return true
    }
}