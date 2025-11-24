package com.ondot.main.setting.nav_map.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.setting.nav_map.NavMapSettingScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object NavMapSettingNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.NavMapSettingGraph
    override val startDestination: String
        get() = NavRoutes.NavMapSetting.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = NavRoutes.NavMapSetting.route,
            route = NavRoutes.NavMapSettingGraph.route
        ) {
            composable(NavRoutes.NavMapSetting.route) {
                NavMapSettingScreen(
                    popScreen = { navController.popBackStack() }
                )
            }
        }
    }
}