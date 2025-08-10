package com.dh.ondot.core.di

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.dh.ondot.core.util.IosAlarmScheduler
import com.dh.ondot.core.util.IosAlarmStorage
import com.dh.ondot.core.util.IosSoundPlayer
import com.dh.ondot.core.util.toUtf8
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.network.TokenProvider
import platform.Foundation.NSBundle
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.WebKit.WKWebView

actual fun provideTokenProvider(): TokenProvider = TokenProvider()

actual fun provideSoundPlayer(): SoundPlayer = IosSoundPlayer()

actual fun provideAlarmStorage(): AlarmStorage = IosAlarmStorage()

actual fun provideAlarmScheduler(): AlarmScheduler = IosAlarmScheduler()

actual fun openDirections(
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

        if (app.canOpenURL(u)) {
            app.openURL(u)
        } else if (fallback != null) {
            app.openURL(NSURL.URLWithString(fallback)!!)
        }
    }

    when (provider) {
        MapProvider.Kakao -> {
            val mode = "publictransit"
            val url = "kakaomap://route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode"
            // 카카오맵 미설치면 모바일웹 스킴으로 우회 가능
            val web = "http://m.map.kakao.com/scheme/route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode"
            open(url, web)
        }
        MapProvider.Naver -> {
            val mode = "public"
            val bundleId = NSBundle.mainBundle.bundleIdentifier ?: "com.example.app"
            val url = "nmap://route/$mode" +
                    "?slat=$startLat&slng=$startLng&sname=${startName.toUtf8()}" +
                    "&dlat=$endLat&dlng=$endLng&dname=${endName.toUtf8()}" +
                    "&appname=$bundleId"
            // 네이버맵 미설치
            open(url, "http://itunes.apple.com/app/id311867728?mt=8")
        }
        MapProvider.Apple -> {
            val dir = "r"
            // Apple 공식 Map Links
            val url = "http://maps.apple.com/?saddr=$startLat,$startLng&daddr=$endLat,$endLng&dirflg=$dir"
            open(url)
        }
    }
}

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {} // Ios에서는 무시

@Composable
actual fun WebView(url: String, modifier: Modifier) {
    val webView = remember { WKWebView() }
    val request = NSMutableURLRequest.requestWithURL(URL = NSURL(string = url))

    webView.apply {
        loadRequest(request)
        allowsBackForwardNavigationGestures = true // Swipe Back 제스쳐
    }

    UIKitView(
        factory = { webView },
        modifier = Modifier.fillMaxSize().then(modifier)
    )
}