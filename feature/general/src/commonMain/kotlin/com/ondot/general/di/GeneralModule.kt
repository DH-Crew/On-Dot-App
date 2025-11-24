package com.ondot.general.di

import com.ondot.general.GeneralScheduleViewModel
import com.ondot.general.navigation.GeneralScheduleNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val generalModule = module {
    viewModelOf(::GeneralScheduleViewModel)
    single<NavGraphContributor>(named("general")) { GeneralScheduleNavGraph }
}