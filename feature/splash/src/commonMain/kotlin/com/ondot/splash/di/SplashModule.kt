package com.ondot.splash.di

import com.ondot.navigation.base.NavGraphContributor
import com.ondot.splash.SplashViewModel
import com.ondot.splash.navigation.SplashNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule = module {
    viewModelOf(::SplashViewModel)
    single<NavGraphContributor>(named("splash")) { SplashNavGraph }
}