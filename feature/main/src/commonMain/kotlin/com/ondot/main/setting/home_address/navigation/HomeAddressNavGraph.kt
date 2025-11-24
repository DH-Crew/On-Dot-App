package com.ondot.main.setting.home_address.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ondot.main.setting.SettingViewModel
import com.ondot.main.setting.home_address.HomeAddressEditScreen
import com.ondot.main.setting.home_address.HomeAddressSettingScreen
import com.ondot.navigation.NavRoutes
import com.ondot.navigation.base.NavGraphContributor
import org.koin.compose.viewmodel.koinViewModel

object HomeAddressNavGraph: NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.HomeAddressSettingGraph
    override val startDestination: String
        get() = NavRoutes.HomeAddressSetting.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            startDestination = startDestination,
            route = graphRoute.route
        ) {
            composable(NavRoutes.HomeAddressSetting.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(graphRoute.route)
                }
                val viewModel: SettingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                HomeAddressSettingScreen(
                    viewModel = viewModel,
                    popScreen = { navController.popBackStack() },
                    navigateToHomeAddressEditScreen = {
                        navController.navigate(NavRoutes.HomeAddressEdit.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(NavRoutes.HomeAddressEdit.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(graphRoute.route)
                }
                val viewModel: SettingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                HomeAddressEditScreen(
                    viewModel = viewModel,
                    popScreen = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}