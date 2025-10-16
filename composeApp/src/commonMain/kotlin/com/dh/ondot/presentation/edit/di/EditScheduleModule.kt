package com.dh.ondot.presentation.edit.di

import com.dh.ondot.presentation.edit.EditScheduleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val editScheduleModule = module {
    viewModel<EditScheduleViewModel> { EditScheduleViewModel(get(), get()) }
}