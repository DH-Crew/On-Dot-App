package com.dh.ondot.core.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.repeat.ScheduleRepeatSettingScreen
import com.dh.ondot.presentation.login.LoginScreen
import com.dh.ondot.presentation.main.MainScreen
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
                },
                navigateToMain = {
                    navController.navigate(NavRoutes.Main.route) {
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

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Main.route,
        route = NavRoutes.MainGraph.route
    ) {
        composable(NavRoutes.Main.route) {
            MainScreen()
        }
    }
}

fun NavGraphBuilder.generalScheduleNavGraph(navController: NavHostController) {
    val graphRoute = NavRoutes.GeneralScheduleGraph.route

    navigation(
        startDestination = NavRoutes.ScheduleRepeatSetting.route,
        route = graphRoute
    ) {
        composable(NavRoutes.ScheduleRepeatSetting.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(graphRoute)
            }
            val viewModel: GeneralScheduleViewModel = viewModel(parentEntry)

            ScheduleRepeatSettingScreen(
                viewModel = viewModel,
                navigateToMain = {
                    navController.navigate(NavRoutes.Main.route) {
                        popUpTo(graphRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}