package com.dh.ondot.core.di

import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.network.NetworkClient
import com.dh.ondot.network.TokenProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single<TokenProvider> { provideTokenProvider() }
    single { NetworkClient(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
}