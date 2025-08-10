package com.dh.ondot

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dh.ondot.core.navigation.NavRoutes
import com.dh.ondot.core.navigation.alarmNavGraph
import com.dh.ondot.core.navigation.deleteAccountNavGraph
import com.dh.ondot.core.navigation.editScheduleNavGraph
import com.dh.ondot.core.navigation.generalScheduleNavGraph
import com.dh.ondot.core.navigation.loginNavGraph
import com.dh.ondot.core.navigation.mainNavGraph
import com.dh.ondot.core.navigation.onboardingNavGraph
import com.dh.ondot.core.navigation.serviceTermsNavGraph
import com.dh.ondot.core.navigation.splashNavGraph
import com.dh.ondot.core.ui.util.DismissKeyboardOnClick
import com.dh.ondot.core.ui.util.ToastHost
import com.dh.ondot.core.util.AlarmNotifier
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.presentation.ui.theme.OnDotTheme

@Composable
fun App() {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        AlarmNotifier.events.collect { event ->
            when(event.type) {
                AlarmType.Departure -> navController.navigate(NavRoutes.DepartureAlarm.createRoute(event.alarmId))
                AlarmType.Preparation -> navController.navigate(NavRoutes.PreparationAlarm.createRoute(event.alarmId))
            }
        }
    }

    OnDotTheme {
        DismissKeyboardOnClick {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.SplashGraph.route,
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
                    alarmNavGraph(navController)
                    splashNavGraph(navController = navController)
                    loginNavGraph(navController = navController)
                    onboardingNavGraph(navController = navController)
                    mainNavGraph(navController = navController)
                    generalScheduleNavGraph(navController = navController)
                    editScheduleNavGraph(navController = navController)
                    deleteAccountNavGraph(navController = navController)
                    serviceTermsNavGraph(navController = navController)
                }

                ToastHost()
            }
        }
    }
}