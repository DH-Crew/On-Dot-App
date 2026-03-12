package com.ondot.everytime.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dh.ondot.presentation.ui.theme.SUCCESS_CREATE_EVERYTIME_SCHEDULE
import com.ondot.domain.model.enums.ToastType
import com.ondot.everytime.LandingRoute
import com.ondot.everytime.contract.EverytimeViewModel
import com.ondot.everytime.placepicker.EverytimePlacePickerRoute
import com.ondot.everytime.timetable.EverytimeTimetableRoute
import com.ondot.everytime.urlInput.EverytimeUrlInputRoute
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.ui.screen.loading.RouteLoadingScreen
import com.ondot.ui.util.ToastManager
import org.koin.compose.viewmodel.koinViewModel

object EverytimeNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.EverytimeGraph
    override val startDestination: String
        get() = NavRoutes.Landing.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.Landing.route) {
                LandingRoute(
                    popScreen = { navController.popBackStack() },
                    navigateToUrlInput = {
                        navController.navigate(NavRoutes.UrlInput.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.UrlInput.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: EverytimeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                EverytimeUrlInputRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToTimetable = {
                        navController.navigate(NavRoutes.Timetable.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.Timetable.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: EverytimeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                EverytimeTimetableRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToPlacePicker = {
                        navController.navigate(NavRoutes.EverytimePlacePicker.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.EverytimePlacePicker.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: EverytimeViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                EverytimePlacePickerRoute(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToRouteLoading = {
                        navController.navigate(NavRoutes.EverytimeRouteLoading.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.EverytimeRouteLoading.route) {
                RouteLoadingScreen(
                    navigateToNext = {
                        ToastManager.tryShow(SUCCESS_CREATE_EVERYTIME_SCHEDULE, ToastType.INFO)
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.EverytimeGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
