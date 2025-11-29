package com.ondot.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.login.LoginScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object LoginNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.LoginGraph
    override val startDestination: String
        get() = NavRoutes.Login.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
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
}