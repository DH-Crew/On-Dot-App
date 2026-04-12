package com.ondot.util.di

import com.ondot.domain.service.ScheduleAlarmManager
import com.ondot.util.DefaultScheduleAlarmManager
import org.koin.dsl.module

val utilModule =
    module {
        single<ScheduleAlarmManager> { DefaultScheduleAlarmManager(get(), get()) }
    }
