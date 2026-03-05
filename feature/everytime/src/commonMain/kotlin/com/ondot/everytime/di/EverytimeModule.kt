package com.ondot.everytime.di

import com.ondot.everytime.navigation.EverytimeNavGraph
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.qualifier.named
import org.koin.dsl.module

val everytimeModule = module {
    single<NavGraphContributor>(named(NavRoutes.EverytimeGraph.route)) { EverytimeNavGraph }
}