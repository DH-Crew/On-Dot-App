package com.ondot.platform.util

import com.dh.ondot.analytics.ONDAnalytics
import com.ondot.domain.service.AnalyticsManager
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSArray
import platform.Foundation.NSDictionary
import platform.Foundation.NSNull
import platform.Foundation.NSNumber
import platform.Foundation.numberWithBool
import platform.Foundation.numberWithDouble
import platform.Foundation.numberWithFloat
import platform.Foundation.numberWithInt
import platform.Foundation.numberWithLongLong

class IosAnalyticsManager(): AnalyticsManager {
    @OptIn(ExperimentalForeignApi::class)
    override fun logEvent(name: String, params: Map<String, Any?>) {
        val boxed: Map<Any?, *> = params.mapValues { (_, v) ->
            when (v) {
                null -> NSNull()
                is String, is NSNumber, is NSArray, is NSDictionary -> v
                is Int -> NSNumber.numberWithInt(v)
                is Long -> NSNumber.numberWithLongLong(v)
                is Float -> NSNumber.numberWithFloat(v)
                is Double -> NSNumber.numberWithDouble(v)
                is Boolean -> NSNumber.numberWithBool(v)
                else -> v.toString() // 기타 타입은 문자열로
            }
        }
        ONDAnalytics.logEvent(name, parameters = boxed)
    }
    @OptIn(ExperimentalForeignApi::class)
    override fun setUserId(id: String?) {
        ONDAnalytics.setUserID(id)
    }
    @OptIn(ExperimentalForeignApi::class)
    override fun setUserProperty(name: String, value: String) {
        ONDAnalytics.setUserProperty(value, forName = name)
    }
}