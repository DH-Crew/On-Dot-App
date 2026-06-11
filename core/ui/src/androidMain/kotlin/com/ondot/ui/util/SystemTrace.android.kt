package com.ondot.ui.util

import android.os.Trace
import com.dh.core.ui.BuildConfig

actual inline fun <T> systemTrace(
    name: String,
    block: () -> T,
): T {
    if (!BuildConfig.DEBUG) {
        return block()
    }

    Trace.beginSection(name)
    return try {
        block()
    } finally {
        Trace.endSection()
    }
}
