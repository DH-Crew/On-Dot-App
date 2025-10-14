package com.dh.ondot.core.ui.util

import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.ui.ToastData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object ToastManager {
    private val _toasts = MutableSharedFlow<ToastData>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val toasts: SharedFlow<ToastData> = _toasts

    suspend fun show(message: String, type: ToastType, duration: Long = 2000L) {
        _toasts.emit(ToastData(message = message, type = type, duration = duration))
    }
}