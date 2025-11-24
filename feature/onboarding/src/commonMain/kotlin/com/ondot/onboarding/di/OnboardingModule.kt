package com.ondot.onboarding.di

import com.ondot.navigation.base.NavGraphContributor
import com.ondot.onboarding.OnboardingViewModel
import com.ondot.onboarding.navigation.OnboardingNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)
    single<NavGraphContributor> { OnboardingNavGraph }
}