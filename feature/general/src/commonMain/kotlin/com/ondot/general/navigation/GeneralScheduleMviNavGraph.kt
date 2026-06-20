package com.ondot.general.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.general.contract.GeneralScheduleViewModel
import com.ondot.general.ui.check.CheckScheduleRoute
import com.ondot.general.ui.place.PlacePickerRoute
import com.ondot.general.ui.repeat.ScheduleRepeatSettingRoute
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.ui.screen.loading.RouteLoadingScreen
import org.koin.compose.viewmodel.koinViewModel

object GeneralScheduleMviNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.GeneralScheduleMviGraph
    override val startDestination: String
        get() = NavRoutes.ScheduleRepeatSettingMvi.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.ScheduleRepeatSettingMvi.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                ScheduleRepeatSettingRoute(
                    viewModel = viewModel,
                    navigateToMain = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(graphRoute.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToPlacePicker = {
                        navController.navigate(NavRoutes.PlacePickerMvi.route) {
                            launchSingleTop = true
                        }
                    },
                    popScreen = { navController.popBackStack() },
                )
            }

            composable(NavRoutes.PlacePickerMvi.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                PlacePickerRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToRouteLoading = {
                        navController.navigate(NavRoutes.RouteLoadingMvi.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.RouteLoadingMvi.route) {
                RouteLoadingScreen(
                    navigateToNext = {
                        navController.navigate(NavRoutes.CheckScheduleMvi.route) {
                            popUpTo(NavRoutes.PlacePickerMvi.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.CheckScheduleMvi.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                CheckScheduleRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToMain = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(graphRoute.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
