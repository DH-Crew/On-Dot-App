package com.dh.ondot.presentation.main.di

import com.dh.ondot.presentation.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel<MainViewModel> { MainViewModel() }
}