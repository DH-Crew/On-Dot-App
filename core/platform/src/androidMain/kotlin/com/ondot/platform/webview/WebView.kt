package com.ondot.platform.webview

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

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