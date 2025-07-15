package com.dh.ondot.core.di

import androidx.compose.runtime.Composable
import com.dh.ondot.core.util.IosSoundPlayer
import com.dh.ondot.domain.di.AlarmScheduler
import com.dh.ondot.domain.di.AlarmStorage
import com.dh.ondot.domain.di.SoundPlayer
import com.dh.ondot.network.TokenProvider

actual fun provideTokenProvider(): TokenProvider = TokenProvider()

actual fun provideSoundPlayer(): SoundPlayer = IosSoundPlayer()

actual fun provideAlarmStorage(): AlarmStorage {
    TODO("Not yet implemented")
}

actual fun provideAlarmScheduler(): AlarmScheduler {
    TODO("Not yet implemented")
}

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {} // Ios에서는 무시