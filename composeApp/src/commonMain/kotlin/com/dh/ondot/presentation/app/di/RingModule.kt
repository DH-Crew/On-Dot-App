package com.dh.ondot.presentation.app.di

import com.dh.ondot.presentation.app.AppViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ringModule = module {
    viewModel<AppViewModel> { AppViewModel(get(), get(), get(), get(), get()) }
}