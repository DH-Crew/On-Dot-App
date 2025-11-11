package com.dh.ondot.presentation.login.di

import com.dh.ondot.presentation.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val loginModule = module {
    viewModel<LoginViewModel> { LoginViewModel(get(), get(), get()) }
}