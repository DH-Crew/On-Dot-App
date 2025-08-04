package com.dh.ondot.core.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.network.TokenProvider

expect fun provideTokenProvider(): TokenProvider

expect fun provideSoundPlayer(): SoundPlayer

expect fun provideAlarmStorage(): AlarmStorage

expect fun provideAlarmScheduler(): AlarmScheduler

@Composable
expect fun BackPressHandler(onBack: () -> Unit)

@Composable
expect fun WebView(url: String, modifier: Modifier = Modifier)