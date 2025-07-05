package com.dh.ondot.core.di

import com.dh.ondot.kakaologin.KakaoLoginHelper
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun kakaoSignIn(onResult: (String) -> Unit) {
    val helper = KakaoLoginHelper()

    helper.login { accessToken, error ->
        if (error == null) {
            onResult(accessToken ?: "")
        } else {
            onResult("")
        }
    }
}