package com.ondot.general.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.general.GeneralScheduleViewModel
import com.ondot.general.check.CheckScheduleScreen
import com.ondot.general.loading.RouteLoadingScreen
import com.ondot.general.place.PlacePickerScreen
import com.ondot.general.repeat.ScheduleRepeatSettingScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import org.koin.compose.viewmodel.koinViewModel

object GeneralScheduleNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.GeneralScheduleGraph
    override val startDestination: String
        get() = NavRoutes.ScheduleRepeatSetting.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(NavRoutes.ScheduleRepeatSetting.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(graphRoute)
                }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                ScheduleRepeatSettingScreen(
                    viewModel = viewModel,
                    navigateToMain = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(graphRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToPlacePicker = {
                        navController.navigate(NavRoutes.PlacePicker.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.PlacePicker.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(graphRoute)
                }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                PlacePickerScreen(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToRouteLoading = {
                        navController.navigate(NavRoutes.RouteLoading.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.RouteLoading.route) {
                RouteLoadingScreen(
                    navigateToCheckSchedule = {
                        navController.navigate(NavRoutes.CheckSchedule.route) {
                            popUpTo(NavRoutes.PlacePicker.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.CheckSchedule.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(graphRoute)
                }
                val viewModel: GeneralScheduleViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                CheckScheduleScreen(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToMain = {
                        navController.navigate(NavRoutes.Main.route) {
                            popUpTo(graphRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}