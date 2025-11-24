package com.ondot.main.setting.account_deletion.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.setting.account_deletion.DeleteAccountScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object DeleteAccountNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.DeleteAccountGraph
    override val startDestination: String
        get() = NavRoutes.DeleteAccount.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = NavRoutes.DeleteAccount.route,
            route = NavRoutes.DeleteAccountGraph.route
        ) {
            composable(NavRoutes.DeleteAccount.route) {
                DeleteAccountScreen(
                    popScreen = { navController.popBackStack() },
                    navigateToLoginScreen = {
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(NavRoutes.DeleteAccountGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}