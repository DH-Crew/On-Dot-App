package com.ondot.edit.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ondot.edit.EditScheduleScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object EditScheduleNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.EditScheduleGraph
    override val startDestination: String
        get() = NavRoutes.EditSchedule.ROUTE

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(
                NavRoutes.EditSchedule.ROUTE,
                arguments = listOf(navArgument("scheduleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavRoutes.EditSchedule>()
                val scheduleId = args.scheduleId

                EditScheduleScreen(
                    scheduleId = scheduleId,
                    popScreen = { navController.popBackStack() }
                )
            }
        }
    }
}