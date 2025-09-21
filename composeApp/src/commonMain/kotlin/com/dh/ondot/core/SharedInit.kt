package com.dh.ondot.core

import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.platform.appModule
import com.dh.ondot.core.platform.provideAlarmScheduler
import com.dh.ondot.core.platform.provideAlarmStorage
import com.dh.ondot.core.platform.provideAnalyticsManager
import com.dh.ondot.core.platform.provideDatabase
import com.dh.ondot.core.platform.provideMapProvider
import com.dh.ondot.core.platform.provideSoundPlayer
import com.dh.ondot.core.platform.provideTokenProvider
import org.koin.core.context.startKoin

fun initShared() {
    ServiceLocator.init(
        provideTokenProvider(),
        provideAlarmStorage(),
        provideAlarmScheduler(),
        provideSoundPlayer(),
        provideMapProvider(),
        provideDatabase(),
        provideAnalyticsManager()
    )
}

fun initKoin() {
    startKoin {
        modules(
            appModule
        )
    }
}