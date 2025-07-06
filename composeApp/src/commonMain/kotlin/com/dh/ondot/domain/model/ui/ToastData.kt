package com.dh.ondot.domain.model.ui

import com.dh.ondot.domain.model.enums.ToastType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ToastData @OptIn(ExperimentalTime::class) constructor(
    val id: Long = Clock.System.now().toEpochMilliseconds(),
    val message: String,
    val type: ToastType,
    val duration: Long = 2000L
)