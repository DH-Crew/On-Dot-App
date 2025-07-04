package com.dh.ondot.di

import com.dh.ondot.network.TokenProvider

actual fun provideTokenProvider(): TokenProvider = TokenProvider()
