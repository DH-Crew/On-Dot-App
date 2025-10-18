package com.ondot.data.di

import com.ondot.data.local.datasource.ScheduleLocalDataSourceImpl
import com.ondot.domain.datasource.ScheduleLocalDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single<ScheduleLocalDataSource> { ScheduleLocalDataSourceImpl(get()) }
}