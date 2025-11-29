package com.ondot.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.splash.SplashScreen

object SplashNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.SplashGraph
    override val startDestination: String
        get() = NavRoutes.Splash.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(NavRoutes.Splash.route) {
                SplashScreen(
                    navigateToLogin = {
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(NavRoutes.Splash.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToHome = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(NavRoutes.Splash.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}