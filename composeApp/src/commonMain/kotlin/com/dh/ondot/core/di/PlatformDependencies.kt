package com.dh.ondot.core.di

import androidx.compose.runtime.Composable
import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.network.TokenProvider

expect fun provideTokenProvider(): TokenProvider

expect fun provideSoundPlayer(): SoundPlayer

@Composable
expect fun BackPressHandler(onBack: () -> Unit)