package com.ondot.onboarding.di

import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.onboarding.navigation.OnboardingMviNavGraph
import com.ondot.onboarding.navigation.OnboardingNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val onboardingModule =
    module {
        viewModelOf(::OnboardingViewModel)
        single<NavGraphContributor>(named("onboarding")) { OnboardingNavGraph }
        single<NavGraphContributor>(named(NavRoutes.OnboardingMviGraph.route)) { OnboardingMviNavGraph }
    }
