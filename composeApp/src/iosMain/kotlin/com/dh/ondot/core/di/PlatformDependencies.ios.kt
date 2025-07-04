package com.dh.ondot.core.di

import com.dh.ondot.network.TokenProvider

actual fun provideTokenProvider(): TokenProvider = TokenProvider()
