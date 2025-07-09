package com.dh.ondot.core

import com.dh.ondot.core.di.ServiceLocator
import com.dh.ondot.core.di.appModule
import com.dh.ondot.core.di.provideTokenProvider
import org.koin.core.context.startKoin

fun initShared() {
    ServiceLocator.init(provideTokenProvider())
}

fun initKoin() {
    startKoin {
        modules(
            appModule
        )
    }
}