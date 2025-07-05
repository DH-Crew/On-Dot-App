package com.dh.ondot.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dh.ondot.presentation.login.LoginScreen
import com.dh.ondot.presentation.onboarding.OnboardingScreen
import com.dh.ondot.presentation.splash.SplashScreen

fun NavGraphBuilder.splashNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Splash.route,
        route = NavRoutes.SplashGraph.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }

                }
            )
        }
    }
}

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Login.route,
        route = NavRoutes.LoginGraph.route
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                navigateToOnboarding = {
                    navController.navigate(NavRoutes.Onboarding.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Onboarding.route,
        route = NavRoutes.OnboardingGraph.route
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen()
        }
    }
}