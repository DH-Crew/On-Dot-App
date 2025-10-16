package com.dh.ondot.di

import android.app.Application
import android.content.Context
import org.koin.dsl.module

fun androidModule(app: Application) = module {
    single<Context> { app.applicationContext }
}