package com.ondot.platform.di

import com.ondot.domain.service.AlarmScheduler
import com.ondot.domain.service.AnalyticsManager
import com.ondot.domain.service.DirectionsOpener
import com.ondot.domain.service.KaKaoSignInProvider
import com.ondot.domain.service.MapProviderStorage
import com.ondot.domain.service.SoundPlayer
import com.ondot.domain.service.TokenProvider
import com.ondot.domain.service.UrlOpener
import com.ondot.platform.kakao.IosKaKaoSignInProvider
import com.ondot.platform.network.IosTokenProvider
import com.ondot.platform.network.httpClient
import com.ondot.platform.util.IosAlarmScheduler
import com.ondot.platform.util.IosAnalyticsManager
import com.ondot.platform.util.IosDirectionsOpener
import com.ondot.platform.util.IosMapProviderStorage
import com.ondot.platform.util.IosSoundPlayer
import com.ondot.platform.util.IosUrlOpener
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providePlatformModules(): List<Module> {
    return listOf(
        module {
            single<HttpClient> { httpClient() }
            single<TokenProvider> { IosTokenProvider() }
            single<UrlOpener> { IosUrlOpener() }
            single<MapProviderStorage> { IosMapProviderStorage() }
            single<AlarmScheduler> { IosAlarmScheduler() }
            single<AnalyticsManager> { IosAnalyticsManager() }
            single<DirectionsOpener> { IosDirectionsOpener() }
            single<SoundPlayer> { IosSoundPlayer() }
            single<KaKaoSignInProvider> { IosKaKaoSignInProvider() }
        }
    )
}