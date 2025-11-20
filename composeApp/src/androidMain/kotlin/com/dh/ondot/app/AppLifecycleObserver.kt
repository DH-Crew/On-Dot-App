package com.dh.ondot.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ondot.util.AnalyticsLogger

object AppLifecycleLogger : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        AnalyticsLogger.logEvent("app_background")
    }

    override fun onStart(owner: LifecycleOwner) {
        AnalyticsLogger.logEvent("app_foreground")
    }
}