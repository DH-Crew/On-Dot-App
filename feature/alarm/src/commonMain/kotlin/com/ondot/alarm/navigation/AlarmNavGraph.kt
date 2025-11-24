package com.ondot.alarm.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ondot.alarm.departure.DepartureAlarmRingScreen
import com.ondot.alarm.preparation.PreparationAlarmRingScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor

object AlarmNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.AlarmGraph
    override val startDestination: String
        get() = NavRoutes.PreparationAlarm.ROUTE

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(
                NavRoutes.PreparationAlarm.ROUTE,
                arguments = listOf(
                    navArgument("scheduleId") { type = NavType.LongType },
                    navArgument("alarmId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavRoutes.PreparationAlarm>()
                val scheduleId = args.scheduleId
                val alarmId = args.alarmId

                PreparationAlarmRingScreen(
                    scheduleId = scheduleId,
                    alarmId = alarmId,
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.AlarmGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )

            }

            composable(
                NavRoutes.DepartureAlarm.ROUTE,
                arguments = listOf(
                    navArgument("scheduleId") { type = NavType.LongType },
                    navArgument("alarmId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val args = backStackEntry.toRoute<NavRoutes.DepartureAlarm>()
                val scheduleId = args.scheduleId
                val alarmId = args.alarmId

                DepartureAlarmRingScreen(
                    scheduleId = scheduleId,
                    alarmId = alarmId,
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.AlarmGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}