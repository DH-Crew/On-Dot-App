package com.ondot.ui.util

actual inline fun <T> systemTrace(
    name: String,
    block: () -> T,
): T = block()
