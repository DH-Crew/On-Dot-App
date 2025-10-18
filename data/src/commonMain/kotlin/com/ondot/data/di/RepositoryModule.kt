package com.ondot.data.di

import com.ondot.data.repository.AuthRepositoryImpl
import com.ondot.data.repository.MemberRepositoryImpl
import com.ondot.data.repository.PlaceRepositoryImpl
import com.ondot.data.repository.ScheduleRepositoryImpl
import com.ondot.domain.repository.AuthRepository
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.repository.PlaceRepository
import com.ondot.domain.repository.ScheduleRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get()) }
    single<PlaceRepository> { PlaceRepositoryImpl(get()) }
    single<MemberRepository> { MemberRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}