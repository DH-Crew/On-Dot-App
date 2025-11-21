package com.ondot.util

import com.ondot.domain.service.AnalyticsManager
import org.koin.mp.KoinPlatform

object AnalyticsLogger {
    private val manager: AnalyticsManager by lazy {
        KoinPlatform.getKoin().get<AnalyticsManager>()
    }

    fun logEvent(event: String, params: Map<String, Any?> = mapOf()) {
        manager.logEvent(event, params)
    }
}