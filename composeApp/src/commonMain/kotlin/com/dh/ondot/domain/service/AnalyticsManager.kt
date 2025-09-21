package com.dh.ondot.domain.service

interface AnalyticsManager {
    fun logEvent(name: String, params: Map<String, Any?> = emptyMap())
    fun setUserId(id: String?)
    fun setUserProperty(name: String, value: String)
}