package com.dh.ondot.core

import com.dh.ondot.presentation.app.di.ringModule
import com.dh.ondot.presentation.edit.di.editScheduleModule
import com.dh.ondot.presentation.general.di.generalModule
import com.dh.ondot.presentation.home.di.homeModule
import com.dh.ondot.presentation.login.di.loginModule
import com.dh.ondot.presentation.main.di.mainModule
import com.dh.ondot.presentation.onboarding.di.onboardingModule
import com.dh.ondot.presentation.setting.di.settingModule
import com.dh.ondot.presentation.splash.di.splashModule
import com.ondot.data.di.dataSourceModule
import com.ondot.data.di.databaseModule
import com.ondot.data.di.provideDriverModule
import com.ondot.data.di.repositoryModule
import com.ondot.network.di.networkModule
import com.ondot.platform.di.providePlatformModules
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
                ringModule,
                generalModule,
                homeModule,
                onboardingModule,
                splashModule,
                editScheduleModule,
                loginModule,
                mainModule,
                settingModule,
                dataSourceModule,
                databaseModule,
                provideDriverModule()
            )
        )
    }
}