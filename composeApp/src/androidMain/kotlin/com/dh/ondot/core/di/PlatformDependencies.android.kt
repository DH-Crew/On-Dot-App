package com.dh.ondot.core.di

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.dh.ondot.core.util.AndroidAlarmScheduler
import com.dh.ondot.core.util.AndroidAlarmStorage
import com.dh.ondot.core.util.AndroidSoundPlayer
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