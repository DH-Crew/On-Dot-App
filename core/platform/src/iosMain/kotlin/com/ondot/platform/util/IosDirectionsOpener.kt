package com.ondot.platform.util

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.service.DirectionsOpener
import com.ondot.util.toUtf8
import platform.Foundation.NSBundle
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class IosDirectionsOpener: DirectionsOpener {
    override fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String
    ) {
        val app = UIApplication.sharedApplication

        fun open(url: String, fallback: String? = null) {
            val u = NSURL.URLWithString(url) ?: return

            ensureMain {
                app.openURL(u, options = emptyMap<Any?, Any?>()) { success ->
                    if (!success && fallback != null) {
                        NSURL.URLWithString(fallback)?.let {
                            app.openURL(it, options = emptyMap<Any?, Any?>()) { /* ignore */ }
                        }
                    }
                }
            }
        }

        when (provider) {
            MapProvider.KAKAO -> {
                val mode = "publictransit"
                val url = "kakaomap://route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode"
                val web = "https://m.map.kakao.com/scheme/route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode"
                open(url, web)
            }
            MapProvider.NAVER -> {
                val mode = "public"
                val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.dh.ondot.iosApp"
                val url = "nmap://route/$mode" +
                        "?slat=$startLat&slng=$startLng&sname=${startName.toUtf8()}" +
                        "&dlat=$endLat&dlng=$endLng&dname=${endName.toUtf8()}" +
                        "&appname=$bundleId"
                // 네이버맵 미설치
                open(url, "http://itunes.apple.com/app/id311867728?mt=8")
            }
            MapProvider.APPLE -> {
                val dir = "r"
                // Apple 공식 Map Links
                val url = "http://maps.apple.com/?saddr=$startLat,$startLng&daddr=$endLat,$endLng&dirflg=$dir"
                open(url)
            }
        }
    }

    private fun ensureMain(block: () -> Unit) {
        if (NSThread.isMainThread) block() else dispatch_async(dispatch_get_main_queue()) { block() }
    }
}