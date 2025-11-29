package com.ondot.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.onboarding.OnboardingScreen

object OnboardingNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.OnboardingGraph
    override val startDestination: String
        get() = NavRoutes.Onboarding.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(NavRoutes.Onboarding.route) {
                OnboardingScreen(
                    navigateToMain = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}