package com.dh.ondot.presentation.home.di

import com.dh.ondot.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel<HomeViewModel> { HomeViewModel(get(), get(), get(), get()) }
}