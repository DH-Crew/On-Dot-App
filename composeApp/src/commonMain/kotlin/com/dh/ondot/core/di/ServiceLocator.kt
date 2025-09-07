package com.dh.ondot.core.di

import com.dh.ondot.core.network.NetworkClient
import com.dh.ondot.core.network.TokenProvider
import com.dh.ondot.data.local.datasource.ScheduleLocalDataSourceImpl
import com.dh.ondot.data.local.db.OndotDatabase
import com.dh.ondot.data.repository.AuthRepositoryImpl
import com.dh.ondot.data.repository.MemberRepositoryImpl
import com.dh.ondot.data.repository.PlaceRepositoryImpl
import com.dh.ondot.data.repository.ScheduleRepositoryImpl
import com.dh.ondot.domain.datasource.ScheduleLocalDataSource
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.domain.repository.ScheduleRepository
import com.dh.ondot.domain.service.AlarmScheduler
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.service.MapProviderStorage
import com.dh.ondot.domain.service.SoundPlayer

object ServiceLocator {
    private lateinit var tokenProvider: TokenProvider
    private lateinit var networkClient: NetworkClient
    private lateinit var alarmStorage: AlarmStorage
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var mapProviderStorage: MapProviderStorage
    private lateinit var database: OndotDatabase

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(networkClient, tokenProvider)
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepositoryImpl(networkClient)
    }

    val memberRepository: MemberRepository by lazy {
        MemberRepositoryImpl(networkClient, mapProviderStorage)
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(networkClient, scheduleLocalDataSource)
    }

    val scheduleLocalDataSource: ScheduleLocalDataSource by lazy {
        ScheduleLocalDataSourceImpl(database)
    }

    fun init(
        tokenProvider: TokenProvider,
        alarmStorage: AlarmStorage,
        alarmScheduler: AlarmScheduler,
        soundPlayer: SoundPlayer,
        mapProviderStorage: MapProviderStorage,
        database: OndotDatabase
    ) {
        this.tokenProvider = tokenProvider
        this.networkClient = NetworkClient(tokenProvider)
        this.alarmStorage = alarmStorage
        this.alarmScheduler = alarmScheduler
        this.soundPlayer = soundPlayer
        this.mapProviderStorage = mapProviderStorage
        this.database = database
    }

    fun provideNetworkClient(): NetworkClient = networkClient
    fun provideTokenProvider(): TokenProvider = tokenProvider
    fun provideAlarmStorage(): AlarmStorage = alarmStorage
    fun provideAlarmScheduler(): AlarmScheduler = alarmScheduler
    fun provideSoundPlayer(): SoundPlayer = soundPlayer
    fun provideMapProviderStorage(): MapProviderStorage = mapProviderStorage
    fun provideDatabase(): OndotDatabase = database
}