package com.dh.ondot.core.di

import com.dh.ondot.data.repository.MemberRepositoryImpl
import com.dh.ondot.data.repository.PlaceRepositoryImpl
import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.network.NetworkClient
import com.dh.ondot.network.TokenProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single<TokenProvider> { provideTokenProvider() }
    single { NetworkClient(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
    single<PlaceRepository> { PlaceRepositoryImpl(get()) }
    single<MemberRepository> { MemberRepositoryImpl(get()) }
}