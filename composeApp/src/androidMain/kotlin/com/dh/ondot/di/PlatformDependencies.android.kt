package com.dh.ondot.di

import com.dh.ondot.network.TokenProvider
import com.dh.ondot.util.AppContextHolder

actual fun provideTokenProvider(): TokenProvider = TokenProvider(context = AppContextHolder.context)