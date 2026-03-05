package com.ondot.everytime.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.everytime.LandingRoute
import com.ondot.everytime.urlInput.EverytimeUrlInputRoute
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object EverytimeNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.EverytimeGraph
    override val startDestination: String
        get() = NavRoutes.Landing.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.Landing.route) {
                LandingRoute(
                    popScreen = { navController.popBackStack() },
                    navigateToUrlInput = {
                        navController.navigate(NavRoutes.UrlInput.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.UrlInput.route) {
                EverytimeUrlInputRoute(
                    popScreen = { navController.popBackStack() },
                )
            }
        }
    }
}
