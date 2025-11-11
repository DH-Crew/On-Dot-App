package com.ondot.platform.util

import android.content.Context
import android.os.Bundle
import com.amplitude.android.Amplitude
import com.google.firebase.analytics.FirebaseAnalytics
import com.ondot.domain.service.AnalyticsManager

class AndroidAnalyticsManager(
    private val context: Context,
    private val amplitude: Amplitude
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

        amplitude.track(
            eventType = name,
            eventProperties = params
                .filterKeys { it.isNotBlank() }
                .mapValues { (_, v) -> normalizeForAmplitude(v) }
        )
    }

    override fun setUserId(id: String?) {
        fa.setUserId(id)
        amplitude.setUserId(id)
    }

    override fun setUserProperty(name: String, value: String) {
        fa.setUserProperty(name, value)
    }

    private fun normalizeForAmplitude(v: Any?): Any? = when (v) {
        is String, is Int, is Long, is Double, is Float, is Boolean, null -> v
        else -> v.toString()
    }
}