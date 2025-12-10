package com.dh.ondot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.ondot.api.AppNavBus
import com.ondot.api.AppNavEvent
import com.ondot.domain.model.enums.AlarmType
import com.ondot.navigation.AppNavHost
import com.ondot.navigation.NavRoutes
import com.ondot.ui.util.DismissKeyboardOnClick
import com.ondot.util.AlarmNotifier

@Composable
fun App() {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        AlarmNotifier.flow().collect { event ->
            when(event.type) {
                AlarmType.Departure -> navController.navigate(NavRoutes.DepartureAlarm.createRoute(event.scheduleId, event.alarmId)) {
                    launchSingleTop = true
                }
                AlarmType.Preparation -> navController.navigate(NavRoutes.PreparationAlarm.createRoute(event.scheduleId, event.alarmId)) {
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        AppNavBus.events.collect { event ->
            when(event) {
                is AppNavEvent.OpenAlarm -> TODO()
                AppNavEvent.OpenGeneralSchedule -> {
                    navController.navigate(NavRoutes.GeneralScheduleGraph.route) {
                        launchSingleTop = true
                    }
                }
                AppNavEvent.OpenToday -> TODO()
            }
        }
    }

    OnDotTheme {
        DismissKeyboardOnClick {
            AppNavHost(navController)
        }
    }
}