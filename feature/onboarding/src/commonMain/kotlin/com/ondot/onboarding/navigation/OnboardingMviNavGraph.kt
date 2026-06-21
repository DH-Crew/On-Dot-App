package com.ondot.onboarding.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.arg.GeneralScheduleNavArg
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.onboarding.ui.AlarmSoundRoute
import com.ondot.onboarding.ui.HomeAddressRoute
import com.ondot.onboarding.ui.MapProviderRoute
import com.ondot.onboarding.ui.PreparationTimeRoute
import org.koin.compose.viewmodel.koinViewModel

object OnboardingMviNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.OnboardingMviGraph
    override val startDestination: String
        get() = NavRoutes.PreparationTime.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.PreparationTime.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: OnboardingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                PreparationTimeRoute(
                    viewModel = viewModel,
                    navigateToHomeAddress = {
                        navController.navigate(NavRoutes.HomeAddress.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(NavRoutes.HomeAddress.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: OnboardingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                HomeAddressRoute(
                    viewModel = viewModel,
                    navigateToAlarmSound = {
                        navController.navigate(NavRoutes.AlarmSound.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToMapProvider = {
                        navController.navigate(NavRoutes.MapProvider.route) {
                            launchSingleTop = true
                        }
                    },
                    popScreen = { navController.popBackStack() },
                )
            }

            composable(NavRoutes.AlarmSound.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: OnboardingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                AlarmSoundRoute(
                    viewModel = viewModel,
                    navigateToMapProvider = {
                        navController.navigate(NavRoutes.MapProvider.route) {
                            launchSingleTop = true
                        }
                    },
                    popScreen = { navController.popBackStack() },
                )
            }

            composable(NavRoutes.MapProvider.route) { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry(graphRoute.route)
                    }
                val viewModel: OnboardingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                MapProviderRoute(
                    viewModel = viewModel,
                    navigateToEverytime = {
                        navController.navigate(NavRoutes.UrlInput.route) {
                            popUpTo(graphRoute.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    navigateToGeneralSchedule = {
                        navController.navigate(
                            NavRoutes.GeneralScheduleMviGraph.createRoute(
                                GeneralScheduleNavArg(isFromOnboarding = true),
                            ),
                        ) {
                            popUpTo(graphRoute.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(graphRoute.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    popScreen = { navController.popBackStack() },
                )
            }
        }
    }
}
