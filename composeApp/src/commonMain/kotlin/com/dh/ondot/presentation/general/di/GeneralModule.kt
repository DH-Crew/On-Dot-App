package com.dh.ondot.presentation.general.di

import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val generalModule = module {
    viewModel<GeneralScheduleViewModel> { GeneralScheduleViewModel(get(), get(), get(), get()) }
}