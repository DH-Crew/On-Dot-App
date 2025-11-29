package com.ondot.main.setting.term.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.setting.term.ServiceTermsScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object ServiceTermNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.ServiceTermsGraph
    override val startDestination: String
        get() = NavRoutes.ServiceTerms.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = NavRoutes.ServiceTerms.route,
            route = NavRoutes.ServiceTermsGraph.route
        ) {
            composable(NavRoutes.ServiceTerms.route) {
                ServiceTermsScreen(
                    popScreen = { navController.popBackStack() }
                )
            }
        }
    }
}