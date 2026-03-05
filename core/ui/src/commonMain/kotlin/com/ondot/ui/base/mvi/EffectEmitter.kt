package com.ondot.ui.base.mvi

import co.touchlab.kermit.Logger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class EffectEmitter<SE : SideEffect>(
    private val logger: Logger,
) {
    private val _flow =
        MutableSharedFlow<SE>(
            replay = 0,
            extraBufferCapacity = 64,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val flow: Flow<SE> = _flow

    suspend fun emit(effect: SE) = _flow.emit(effect)

    fun tryEmit(effect: SE) {
        val ok = _flow.tryEmit(effect)
        if (!ok) logger.w { "SideEffect dropped: $effect" }
    }
}
