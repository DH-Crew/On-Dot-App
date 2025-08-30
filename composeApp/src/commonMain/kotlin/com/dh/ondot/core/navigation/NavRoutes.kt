package com.dh.ondot.core.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class NavRoutes(@Transient val route: String = "") {
    // Alarm
    @Serializable
    data object AlarmGraph: NavRoutes("alarmGraph")
    @Serializable
    data class PreparationAlarm(val alarmId: Long) : NavRoutes("preparationAlarm/{alarmId}") {
        companion object {
            const val ROUTE: String = "preparationAlarm/{alarmId}"
            fun createRoute(id: Long) = "preparationAlarm/$id"
        }
    }
    @Serializable
    data class DepartureAlarm(val alarmId: Long) : NavRoutes("departureAlarm/{alarmId}") {
        companion object {
            const val ROUTE: String = "departureAlarm/{alarmId}"
            fun createRoute(id: Long) = "departureAlarm/$id"
        }
    }

    // Splash
    data object SplashGraph: NavRoutes("splashGraph")
    data object Splash: NavRoutes("splash")

    // Login
    data object LoginGraph: NavRoutes("loginGraph")
    data object Login: NavRoutes("login")

    // Onboarding
    data object OnboardingGraph: NavRoutes("onboardingGraph")
    data object Onboarding: NavRoutes("onboarding")

    // Main
    data object MainGraph: NavRoutes("mainGraph")
    data object Main: NavRoutes("main")
    data object Home: NavRoutes("home")
    data object Setting: NavRoutes("setting")

    // General
    data object GeneralScheduleGraph: NavRoutes("generalScheduleGraph")
    data object ScheduleRepeatSetting: NavRoutes("scheduleRepeatSetting")
    data object PlacePicker: NavRoutes("placePicker")
    data object RouteLoading: NavRoutes("routeLoading")
    data object CheckSchedule: NavRoutes("checkSchedule")

    // EditSchedule
    @Serializable
    data object EditScheduleGraph: NavRoutes("editScheduleGraph")
    @Serializable
    data class EditSchedule(val scheduleId: Long): NavRoutes("editSchedule/{scheduleId}") {
        companion object {
            const val ROUTE: String = "editSchedule/{scheduleId}"
            fun createRoute(id: Long) = "editSchedule/$id"
        }
    }

    // DeleteAccount
    data object DeleteAccountGraph: NavRoutes("deleteAccountGraph")
    data object DeleteAccount: NavRoutes("deleteAccount")

    // ServiceTerms
    data object ServiceTermsGraph: NavRoutes("serviceTermsGraph")
    data object ServiceTerms: NavRoutes("serviceTerms")

    // HomeAddressSetting
    data object HomeAddressSettingGraph: NavRoutes("homeAddressSettingGraph")
    data object HomeAddressSetting: NavRoutes("homeAddressSetting")
    data object HomeAddressEdit: NavRoutes("homeAddressEdit")

    // NavMapSetting
    data object NavMapSettingGraph: NavRoutes("navMapSettingGraph")
    data object NavMapSetting: NavRoutes("navMapSetting")

    // PreparationTimeSetting
    data object PreparationTimeSettingGraph: NavRoutes("preparationTimeSettingGraph")
    data object PreparationTimeEdit: NavRoutes("preparationTimeEdit")
}