package com.ondot.edit.di

import com.ondot.edit.EditScheduleViewModel
import com.ondot.edit.navigation.EditScheduleNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val editScheduleModule = module {
    viewModelOf(::EditScheduleViewModel)
    single<NavGraphContributor> { EditScheduleNavGraph }
}