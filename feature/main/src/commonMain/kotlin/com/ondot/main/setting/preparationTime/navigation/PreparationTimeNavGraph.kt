package com.ondot.main.setting.preparationTime.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.setting.preparationTime.PreparationTimeEditScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object PreparationTimeNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.PreparationTimeSettingGraph
    override val startDestination: String
        get() = NavRoutes.PreparationTimeEdit.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = NavRoutes.PreparationTimeEdit.route,
            route = NavRoutes.PreparationTimeSettingGraph.route,
        ) {
            composable(NavRoutes.PreparationTimeEdit.route) {
                PreparationTimeEditScreen(
                    popScreen = { navController.popBackStack() },
                )
            }
        }
    }
}
