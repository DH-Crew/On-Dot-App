package com.dh.ondot.core.di

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.dh.ondot.core.util.IosAlarmScheduler
import com.dh.ondot.core.util.IosAlarmStorage
import com.dh.ondot.core.util.IosSoundPlayer
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.network.TokenProvider
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.WebKit.WKWebView

actual fun provideTokenProvider(): TokenProvider = TokenProvider()

actual fun provideSoundPlayer(): SoundPlayer = IosSoundPlayer()

actual fun provideAlarmStorage(): AlarmStorage = IosAlarmStorage()

actual fun provideAlarmScheduler(): AlarmScheduler = IosAlarmScheduler()

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