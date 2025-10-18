package com.dh.ondot.presentation.setting.di

import com.dh.ondot.presentation.setting.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingModule = module {
    viewModel<SettingViewModel> { SettingViewModel(get(), get(), get(), get()) }
}