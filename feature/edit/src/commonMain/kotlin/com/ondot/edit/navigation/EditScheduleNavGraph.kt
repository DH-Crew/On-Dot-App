package com.ondot.edit.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ondot.edit.EditPlacePickerRoute
import com.ondot.edit.EditScheduleScreen
import com.ondot.edit.EditScheduleViewModel
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.ui.screen.loading.RouteLoadingScreen
import org.koin.compose.viewmodel.koinViewModel

object EditScheduleNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.EditScheduleGraph
    override val startDestination: String
        get() = NavRoutes.EditSchedule.ROUTE

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route,
        ) {
            composable(
                NavRoutes.EditSchedule.ROUTE,
                arguments = listOf(navArgument("scheduleId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: EditScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
                val args = backStackEntry.toRoute<NavRoutes.EditSchedule>()
                val scheduleId = args.scheduleId

                EditScheduleScreen(
                    scheduleId = scheduleId,
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToPlacePicker = {
                        navController.navigate(NavRoutes.EditPlacePicker.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToRouteLoading = {
                        navController.navigate(NavRoutes.EditRouteLoading.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.EditPlacePicker.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: EditScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                EditPlacePickerRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToRouteLoading = {
                        navController.navigate(NavRoutes.EditRouteLoading.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.EditRouteLoading.route) {
                RouteLoadingScreen(
                    navigateToNext = {
                        navController.popBackStack(NavRoutes.EditSchedule.ROUTE, inclusive = false)
                    },
                )
            }
        }
    }
}
