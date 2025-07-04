package com.dh.ondot.core.di

import com.dh.ondot.data.repository.AuthRepositoryImpl
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.network.NetworkClient
import com.dh.ondot.network.TokenProvider

object ServiceLocator {
    private lateinit var tokenProvider: TokenProvider
    private lateinit var networkClient: NetworkClient

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(networkClient)
    }

    fun init(tokenProvider: TokenProvider) {
        this.tokenProvider = tokenProvider
        this.networkClient = NetworkClient(tokenProvider)
    }

    fun provideNetworkClient(): NetworkClient = networkClient
    fun provideTokenProvider(): TokenProvider = tokenProvider
}