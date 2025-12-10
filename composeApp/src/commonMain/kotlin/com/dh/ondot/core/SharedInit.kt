package com.dh.ondot.core

import com.ondot.alarm.di.alarmModule
import com.ondot.api.DeepLinkKit
import com.ondot.api_impl.DefaultDeepLinkParser
import com.ondot.data.di.dataSourceModule
import com.ondot.data.di.databaseModule
import com.ondot.data.di.provideDriverModule
import com.ondot.data.di.repositoryModule
import com.ondot.edit.di.editScheduleModule
import com.ondot.general.di.generalModule
import com.ondot.login.di.loginModule
import com.ondot.main.di.mainModule
import com.ondot.network.di.networkModule
import com.ondot.onboarding.di.onboardingModule
import com.ondot.platform.di.providePlatformModules
import com.ondot.splash.di.splashModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(extraModules: List<Module> = emptyList()) {
    startKoin {
        modules(
            extraModules +
            providePlatformModules() +
            listOf(
                repositoryModule,
                networkModule,
                dataSourceModule,
                databaseModule,
                splashModule,
                loginModule,
                alarmModule,
                editScheduleModule,
                generalModule,
                mainModule,
                onboardingModule,
                provideDriverModule()
            )
        )
    }
}

/**
 * Swift에서 접근하기 위한 메서드
 * */
fun initDeepLinks() {
    DeepLinkKit.install(DefaultDeepLinkParser())
}