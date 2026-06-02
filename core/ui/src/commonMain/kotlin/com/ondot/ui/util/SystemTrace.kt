package com.ondot.ui.util

expect inline fun <T> systemTrace(
    name: String,
    block: () -> T,
): T
