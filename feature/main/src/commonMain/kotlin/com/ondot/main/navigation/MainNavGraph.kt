package com.ondot.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.MainScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object MainNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.MainGraph
    override val startDestination: String
        get() = NavRoutes.Main.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(NavRoutes.Main.route) {
                MainScreen(
                    navigateToGeneralSchedule = {
                        navController.navigate(NavRoutes.GeneralScheduleGraph.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToEditSchedule = { id ->
                        navController.navigate(NavRoutes.EditSchedule.createRoute(id)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToLogin = {
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(NavRoutes.Main.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToDeleteAccount = {
                        navController.navigate(NavRoutes.DeleteAccountGraph.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToServiceTerms = {
                        navController.navigate(NavRoutes.ServiceTerms.createRoute(false)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToHomeAddressSetting = {
                        navController.navigate(NavRoutes.HomeAddressSettingGraph.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToNavMapSetting = {
                        navController.navigate(NavRoutes.NavMapSettingGraph.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToPreparationTimeEdit = {
                        navController.navigate(NavRoutes.PreparationTimeSettingGraph.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToNotification = {
                        navController.navigate(NavRoutes.ServiceTerms.createRoute(true)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}