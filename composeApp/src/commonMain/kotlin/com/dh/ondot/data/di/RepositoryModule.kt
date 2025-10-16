package com.dh.ondot.data.di

import com.dh.ondot.data.repository.AuthRepositoryImpl
import com.dh.ondot.data.repository.MemberRepositoryImpl
import com.dh.ondot.data.repository.PlaceRepositoryImpl
import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.ondot.domain.repository.AuthRepository
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.network.NetworkClient
import com.ondot.platform.network.TokenProvider
import org.koin.dsl.module

val repositoryModule = module {
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get()) }
    single<PlaceRepository> { PlaceRepositoryImpl(get()) }
    single<MemberRepository> { MemberRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get<NetworkClient>(), get<TokenProvider>()) }
}