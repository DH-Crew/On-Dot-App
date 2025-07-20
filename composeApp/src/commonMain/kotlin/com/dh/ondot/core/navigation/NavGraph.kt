package com.dh.ondot.core.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dh.ondot.core.di.GeneralScheduleViewModelFactory
import com.dh.ondot.presentation.alarm.preparation.PreparationAlarmRingScreen
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.check.CheckScheduleScreen
import com.dh.ondot.presentation.general.loading.RouteLoadingScreen
import com.dh.ondot.presentation.general.place.PlacePickerScreen
import com.dh.ondot.presentation.general.repeat.ScheduleRepeatSettingScreen
import com.dh.ondot.presentation.login.LoginScreen
import com.dh.ondot.presentation.main.MainScreen
import com.dh.ondot.presentation.onboarding.OnboardingScreen
import com.dh.ondot.presentation.splash.SplashScreen

fun NavGraphBuilder.alarmNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = NavRoutes.PreparationAlarm.ROUTE,
        route = NavRoutes.AlarmGraph.route
    ) {
        composable(
            NavRoutes.PreparationAlarm.ROUTE,
            arguments = listOf(navArgument("alarmId") { type = NavType.LongType })
        ) { backStackEntry ->
            val args = backStackEntry.toRoute<NavRoutes.PreparationAlarm>()
            val alarmId = args.alarmId

            PreparationAlarmRingScreen(
                alarmId = alarmId,
                navigateToSplash = {
                    navController.navigate(NavRoutes.Splash.route) {
                        popUpTo(NavRoutes.AlarmGraph.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.splashNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Splash.route,
        route = NavRoutes.SplashGraph.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }

                }
            )
        }
    }
}

fun NavGraphBuilder.loginNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Login.route,
        route = NavRoutes.LoginGraph.route
    ) {
        composable(NavRoutes.Login.route) {
            LoginScreen(
                navigateToOnboarding = {
                    navController.navigate(NavRoutes.Onboarding.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToMain = {
                    navController.navigate(NavRoutes.Main.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.onboardingNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Onboarding.route,
        route = NavRoutes.OnboardingGraph.route
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen()
        }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoutes.Main.route,
        route = NavRoutes.MainGraph.route
    ) {
        composable(NavRoutes.Main.route) {
            MainScreen(
                navigateToGeneralSchedule = {
                    navController.navigate(NavRoutes.GeneralScheduleGraph.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.generalScheduleNavGraph(navController: NavHostController) {
    val graphRoute = NavRoutes.GeneralScheduleGraph.route

    navigation(
        startDestination = NavRoutes.ScheduleRepeatSetting.route,
        route = graphRoute
    ) {

        composable(NavRoutes.ScheduleRepeatSetting.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(graphRoute)
            }
            val factory = remember {
                GeneralScheduleViewModelFactory()
            }
            val viewModel: GeneralScheduleViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = factory)

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
            val factory = remember {
                GeneralScheduleViewModelFactory()
            }
            val viewModel: GeneralScheduleViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = factory)

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
            val factory = remember {
                GeneralScheduleViewModelFactory()
            }
            val viewModel: GeneralScheduleViewModel = viewModel(viewModelStoreOwner = parentEntry, factory = factory)

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