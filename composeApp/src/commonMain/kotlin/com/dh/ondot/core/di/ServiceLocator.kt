package com.dh.ondot.core.di

import com.dh.ondot.data.repository.AuthRepositoryImpl
import com.dh.ondot.data.repository.MemberRepositoryImpl
import com.dh.ondot.data.repository.PlaceRepositoryImpl
import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.dh.ondot.domain.di.AlarmScheduler
import com.dh.ondot.domain.di.AlarmStorage
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.network.NetworkClient
import com.dh.ondot.network.TokenProvider

object ServiceLocator {
    private lateinit var tokenProvider: TokenProvider
    private lateinit var networkClient: NetworkClient
    private lateinit var alarmStorage: AlarmStorage
    private lateinit var alarmScheduler: AlarmScheduler

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(networkClient, tokenProvider)
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepositoryImpl(networkClient)
    }

    val memberRepository: MemberRepository by lazy {
        MemberRepositoryImpl(networkClient)
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(networkClient)
    }

    fun init(
        tokenProvider: TokenProvider,
        alarmStorage: AlarmStorage,
        alarmScheduler: AlarmScheduler
    ) {
        this.tokenProvider = tokenProvider
        this.networkClient = NetworkClient(tokenProvider)
        this.alarmStorage = alarmStorage
        this.alarmScheduler = alarmScheduler
    }

    fun provideNetworkClient(): NetworkClient = networkClient
    fun provideTokenProvider(): TokenProvider = tokenProvider
    fun provideAlarmStorage(): AlarmStorage = alarmStorage
    fun provideAlarmScheduler(): AlarmScheduler = alarmScheduler
}