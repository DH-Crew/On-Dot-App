package com.ondot.alarm.di

import com.ondot.alarm.AlarmViewModel
import com.ondot.alarm.navigation.AlarmNavGraph
import com.ondot.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val alarmModule = module {
    viewModelOf(::AlarmViewModel)
    single<NavGraphContributor>(named("alarm")) { AlarmNavGraph }
}