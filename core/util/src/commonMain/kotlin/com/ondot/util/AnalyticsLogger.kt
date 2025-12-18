package com.ondot.util

import com.ondot.domain.service.AnalyticsManager
import org.koin.mp.KoinPlatform

object AnalyticsLogger {
    private val manager: AnalyticsManager by lazy {
        KoinPlatform.getKoin().get<AnalyticsManager>()
    }

    fun logScreenView(screenName: String, params: Map<String, Any?> = emptyMap()) {
        manager.logEvent(
            name = "screen_view",
            params = params + mapOf(
                "screen_name" to screenName
            )
        )
    }

    fun logEvent(event: String, params: Map<String, Any?> = mapOf()) {
        manager.logEvent(event, params)
    }
}