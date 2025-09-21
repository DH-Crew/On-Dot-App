package com.dh.ondot.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dh.ondot.core.network.TokenProvider
import com.dh.ondot.data.local.db.OndotDatabase
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.AnalyticsManager
import com.dh.ondot.domain.service.MapProviderStorage
import com.dh.ondot.domain.service.SoundPlayer

expect fun provideTokenProvider(): TokenProvider

expect fun provideSoundPlayer(): SoundPlayer

expect fun provideAlarmStorage(): AlarmStorage

expect fun provideAlarmScheduler(): AlarmScheduler

expect fun provideDatabase(): OndotDatabase

expect fun provideAnalyticsManager(): AnalyticsManager

expect fun openDirections(
    startLat: Double,
    startLng: Double,
    endLat: Double,
    endLng: Double,
    provider: MapProvider,
    startName: String,
    endName: String
)

// 안드로이드에서 알람 서비스를 정리하기 위한 메서드
expect fun stopService(alarmId: Long)

expect fun provideMapProvider(): MapProviderStorage

expect fun openUrl(url: String)

@Composable
expect fun BackPressHandler(onBack: () -> Unit)

@Composable
expect fun WebView(url: String, modifier: Modifier = Modifier)