package com.ondot.network.di

import com.ondot.network.NetworkClient
import org.koin.dsl.module

val networkModule = module {
    single<NetworkClient> { NetworkClient(get()) }
}