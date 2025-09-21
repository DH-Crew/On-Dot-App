package com.dh.ondot.core.util

import android.content.Context
import android.os.Bundle
import com.dh.ondot.domain.service.AnalyticsManager
import com.google.firebase.analytics.FirebaseAnalytics

class AndroidAnalyticsManager(
    private val context: Context
): AnalyticsManager {

    private val fa: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(context) }

    override fun logEvent(
        name: String,
        params: Map<String, Any?>
    ) {
        val b = Bundle()
        params.forEach { (k, v) ->
            when (v) {
                null -> {}
                is String -> b.putString(k, v)
                is Int -> b.putInt(k, v)
                is Long -> b.putLong(k, v)
                is Double -> b.putDouble(k, v)
                is Float -> b.putFloat(k, v)
                is Boolean -> b.putInt(k, if (v) 1 else 0)
                else -> b.putString(k, v.toString())
            }
        }
        fa.logEvent(name, b)
    }

    override fun setUserId(id: String?) {
        fa.setUserId(id)
    }

    override fun setUserProperty(name: String, value: String) {
        fa.setUserProperty(name, value)
    }
}