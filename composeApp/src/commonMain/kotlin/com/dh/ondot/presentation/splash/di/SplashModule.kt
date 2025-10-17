package com.dh.ondot.presentation.splash.di

import com.dh.ondot.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val splashModule = module {
    viewModelOf(::SplashViewModel)
}