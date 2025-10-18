package com.ondot.platform.di

import android.content.Context
import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AlarmStorage
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.DirectionsOpener
import com.ondot.domain.service.KaKaoSignInProvider
import com.ondot.domain.service.MapProviderStorage
import com.ondot.domain.service.SoundPlayer
import com.ondot.domain.service.TokenProvider
import com.ondot.domain.service.UrlOpener
import com.ondot.platform.data.OnDotDataStore
import com.ondot.platform.kakao.AndroidKaKaoSignInProvider
import com.ondot.platform.network.AndroidTokenProvider
import com.ondot.platform.network.httpClient
import com.ondot.platform.util.AlarmReceiver
import com.ondot.platform.util.AlarmService
import com.ondot.platform.util.AndroidAlarmScheduler
import com.ondot.platform.util.AndroidAlarmStorage
import com.ondot.platform.util.AndroidAnalyticsManager
import com.ondot.platform.util.AndroidDirectionsOpener
import com.ondot.platform.util.AndroidMapProviderStorage
import com.ondot.platform.util.AndroidSoundPlayer
import com.ondot.platform.util.AndroidUrlOpener
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providePlatformModules(): List<Module> {
    return listOf(
        module {
            single<HttpClient> { httpClient() }
            single<OnDotDataStore> { OnDotDataStore(get()) }
            single<TokenProvider> { AndroidTokenProvider(get()) }
            single<AlarmReceiver> { AlarmReceiver() }
            single<AlarmService> { AlarmService() }
            single<AlarmScheduler> { AndroidAlarmScheduler(get<Context>()) }
            single<AlarmStorage> { AndroidAlarmStorage(get<Context>()) }
            single<AnalyticsManager> { AndroidAnalyticsManager(get<Context>()) }
            single<MapProviderStorage> { AndroidMapProviderStorage(get<Context>()) }
            single<SoundPlayer> { AndroidSoundPlayer(get<Context>()) }
            single<DirectionsOpener> { AndroidDirectionsOpener(get<Context>()) }
            single<UrlOpener> { AndroidUrlOpener(get<Context>()) }
            single<KaKaoSignInProvider> { AndroidKaKaoSignInProvider(get<Context>()) }
        }
    )
}