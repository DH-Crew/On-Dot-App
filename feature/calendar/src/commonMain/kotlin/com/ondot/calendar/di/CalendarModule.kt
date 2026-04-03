package com.ondot.calendar.di

import com.ondot.calendar.contract.CalendarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val calendarModule =
    module {
        viewModelOf(::CalendarViewModel)
    }
