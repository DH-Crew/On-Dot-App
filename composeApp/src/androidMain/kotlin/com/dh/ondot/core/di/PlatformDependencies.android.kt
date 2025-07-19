package com.dh.ondot.core.di

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.dh.ondot.core.util.AndroidAlarmScheduler
import com.dh.ondot.core.util.AndroidAlarmStorage
import com.dh.ondot.core.util.AndroidSoundPlayer
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.SoundPlayer
import com.dh.ondot.network.TokenProvider
import com.dh.ondot.util.AppContextHolder

actual fun provideTokenProvider(): TokenProvider {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return TokenProvider(context = context)
}

actual fun provideSoundPlayer(): SoundPlayer {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidSoundPlayer(context)
}

actual fun provideAlarmStorage(): AlarmStorage {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidAlarmStorage(context)
}

actual fun provideAlarmScheduler(): AlarmScheduler {
    val context = runCatching { AppContextHolder.context }
        .getOrElse { error("AppContextHolder.context가 아직 초기화되지 않았습니다.") }
    return AndroidAlarmScheduler(context)
}

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
}