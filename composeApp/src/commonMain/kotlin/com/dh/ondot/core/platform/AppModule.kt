package com.dh.ondot.core.platform

import com.dh.ondot.core.network.NetworkClient
import com.dh.ondot.core.network.TokenProvider
import com.dh.ondot.data.repository.MemberRepositoryImpl
import com.dh.ondot.data.repository.PlaceRepositoryImpl
import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: Module = module {
    single<TokenProvider> { provideTokenProvider() }
    single { NetworkClient(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get()) }
    single<PlaceRepository> { PlaceRepositoryImpl(get()) }
    single<MemberRepository> { MemberRepositoryImpl(get(), get()) }
}