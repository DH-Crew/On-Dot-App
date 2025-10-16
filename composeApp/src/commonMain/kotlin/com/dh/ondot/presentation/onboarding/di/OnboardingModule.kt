package com.dh.ondot.presentation.onboarding.di

import com.dh.ondot.presentation.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel<OnboardingViewModel> { OnboardingViewModel(get(), get(), get(), get()) }
}