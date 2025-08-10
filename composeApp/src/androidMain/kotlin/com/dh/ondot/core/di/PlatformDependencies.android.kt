package com.dh.ondot.core.di

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.dh.ondot.core.util.AndroidAlarmScheduler
import com.dh.ondot.core.util.AndroidAlarmStorage
import com.dh.ondot.core.util.AndroidSoundPlayer
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.network.TokenProvider
import com.dh.ondot.util.AppContextHolder

actual fun provideTokenProvider(): TokenProvider {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return TokenProvider(context = context)
}

actual fun provideSoundPlayer(): SoundPlayer {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidSoundPlayer(context)
}

actual fun provideAlarmStorage(): AlarmStorage {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidAlarmStorage(context)
}

actual fun provideAlarmScheduler(): AlarmScheduler {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidAlarmScheduler(context)
}

actual fun openDirections(
    startLat: Double,
    startLng: Double,
    endLat: Double,
    endLng: Double,
    provider: MapProvider,
    startName: String,
    endName: String
) {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    val mode = when (provider) {
        MapProvider.Kakao -> "publictransit"
        MapProvider.Naver -> "public"
        MapProvider.Apple -> TODO()
    }
    val intent = when(provider) {
        MapProvider.Kakao -> Intent(Intent.ACTION_VIEW,
            "kakaomap://route?sp=$startLat,$startLng&ep=$endLat,$endLng&by=$mode".toUri()).apply { `package` = "net.daum.android.map" }
        MapProvider.Naver -> Intent(Intent.ACTION_VIEW, ("nmap://route/$mode" +
                "?slat=$startLat&slng=$startLng&sname=${Uri.encode(startName)}" +
                "&dlat=$endLat&dlng=$endLng&dname=${Uri.encode(endName)}" +
                "&appname=${context.packageName}").toUri()).apply { `package` = "com.nhn.android.nmap" }
        MapProvider.Apple -> TODO()
    }

    val pm: PackageManager = context.packageManager
    val canHandle = intent.resolveActivity(pm) != null

    if (canHandle) {
        context.startActivity(intent.addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } else {
        val storeUri = when (provider) {
            MapProvider.Kakao -> "market://details?id=net.daum.android.map"
            MapProvider.Naver -> "market://details?id=com.nhn.android.nmap"
            MapProvider.Apple -> TODO()
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, storeUri.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun WebView(url: String, modifier: Modifier) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled    = true
                    domStorageEnabled    = true    // localStorage/sessionStorage 켜기
                    mixedContentMode     = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                    val defaultUA = userAgentString
                    userAgentString = defaultUA
                        .replace("; wv", "")
                        .replace("Version/4.0", "Chrome/115.0.0.0")
                        .let { "$it Mobile Safari/537.36" }

                    useWideViewPort     = true
                    loadWithOverviewMode= true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                }
                CookieManager
                    .getInstance()
                    .setAcceptThirdPartyCookies(this, true)

                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()

                loadUrl(url)
            }
        },
        update = { view ->
            if (view.url != url) view.loadUrl(url)
        },
        modifier = modifier
    )
}