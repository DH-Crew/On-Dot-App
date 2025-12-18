package com.ondot.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.ui.util.ToastHost
import com.ondot.util.AnalyticsLogger
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import org.koin.compose.getKoin

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val koin = getKoin()
    val contributors = remember { // remember로 재구성 시 새로운 객체 생성 방지
        koin.getAll<NavGraphContributor>().sortedBy { it.priority }
    }
    val start = contributors
        .firstOrNull { it.graphRoute == NavRoutes.SplashGraph }
        ?.graphRoute
        ?: error("해당 Graph를 찾을 수 없습니다.")

    NavScreenViewLogger(navController, contributors)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = start.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            contributors.forEach { with(it) { registerGraph(navController) } }
        }

        ToastHost()
    }
}

@Composable
private fun NavScreenViewLogger(
    navController: NavHostController,
    contributors: List<NavGraphContributor>
) {
    val graphRoutes = remember(contributors) {
        contributors.map { it.graphRoute.route }.toSet()
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .map { it.destination.route.orEmpty() }
            .filter { it.isNotBlank() }
            .filterNot { it in graphRoutes }                 // graphRoute는 제외
            .distinctUntilChanged()                          // 같은 route 연속 중복 방지
            .collect { routePattern ->
                val eventName = routePattern.toScreenViewEventName()
                if (eventName.isNotBlank()) AnalyticsLogger.logEvent(eventName)
            }
    }
}

private fun String.toScreenViewEventName(): String = when (this) {
    NavRoutes.Splash.route -> "screen_view_splash"
    NavRoutes.Login.route -> "screen_view_login"
    NavRoutes.Onboarding.route -> "screen_view_onboarding"
    NavRoutes.Home.route -> "screen_view_home"
    NavRoutes.Setting.route -> "screen_view_setting"

    // 패턴 라우트는 ROUTE로 매칭
    NavRoutes.EditSchedule.ROUTE -> "screen_view_edit_schedule"
    NavRoutes.PreparationAlarm.ROUTE -> "screen_view_preparation_alarm"
    NavRoutes.DepartureAlarm.ROUTE -> "screen_view_departure_alarm"
    NavRoutes.ServiceTerms.ROUTE -> "screen_view_service_terms"

    NavRoutes.ScheduleRepeatSetting.route -> "screen_view_schedule_repeat_setting"
    NavRoutes.PlacePicker.route -> "screen_view_place_picker"
    NavRoutes.RouteLoading.route -> "screen_view_route_loading"
    NavRoutes.CheckSchedule.route -> "screen_view_check_schedule"

    NavRoutes.DeleteAccount.route -> "screen_view_delete_account"
    NavRoutes.HomeAddressSetting.route -> "screen_view_home_address_setting"
    NavRoutes.HomeAddressEdit.route -> "screen_view_home_address_edit"
    NavRoutes.NavMapSetting.route -> "screen_view_nav_map_setting"
    NavRoutes.PreparationTimeEdit.route -> "screen_view_preparation_time_edit"

    else -> ""
}