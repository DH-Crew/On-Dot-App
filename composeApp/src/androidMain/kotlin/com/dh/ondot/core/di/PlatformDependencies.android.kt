package com.dh.ondot.di

import com.dh.ondot.network.TokenProvider
import com.dh.ondot.util.AppContextHolder

actual fun provideTokenProvider(): TokenProvider {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return TokenProvider(context = context)
}