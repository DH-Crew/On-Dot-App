package com.dh.ondot.core.di

import androidx.compose.runtime.Composable
import com.dh.ondot.core.util.IosSoundPlayer
import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.network.TokenProvider

actual fun provideTokenProvider(): TokenProvider = TokenProvider()

actual fun provideSoundPlayer(): SoundPlayer = IosSoundPlayer()

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {} // Ios에서는 무시