package com.ondot.main.setting.term.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ondot.main.setting.term.ServiceTermsScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object ServiceTermNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.ServiceTermsGraph
    override val startDestination: String
        get() = NavRoutes.ServiceTerms.ROUTE

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(
                NavRoutes.ServiceTerms.ROUTE,
                arguments = listOf(navArgument("isNotification") { type = NavType.BoolType })
            ) {backStackEntry ->
                val args = backStackEntry.toRoute<NavRoutes.ServiceTerms>()
                val isNotification = args.isNotification

                ServiceTermsScreen(
                    isNotification = isNotification,
                    popScreen = { navController.popBackStack() }
                )
            }
        }
    }
}