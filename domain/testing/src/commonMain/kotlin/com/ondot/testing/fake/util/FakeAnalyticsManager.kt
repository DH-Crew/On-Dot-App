package com.ondot.testing.fake.util

import com.ondot.domain.service.AnalyticsManager

class FakeAnalyticsManager: AnalyticsManager {
    val events = mutableListOf<Pair<String, Map<String, Any?>>>()

    override fun logEvent(
        name: String,
        params: Map<String, Any?>
    ) {
        events += name to params
    }

    override fun setUserId(id: String?) {
        TODO("Not yet implemented")
    }

    override fun setUserProperty(name: String, value: String) {
        TODO("Not yet implemented")
    }
}