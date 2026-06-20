package com.ondot.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class NavRoutes(
    @Transient val route: String = "",
) {
    // Alarm
    @Serializable
    data object AlarmGraph : NavRoutes("alarmGraph")

    @Serializable
    data class PreparationAlarm(
        val scheduleId: Long,
        val alarmId: Long,
    ) : NavRoutes("preparationAlarm/{scheduleId}/{alarmId}") {
        companion object {
            const val ROUTE: String = "preparationAlarm/{scheduleId}/{alarmId}"

            fun createRoute(
                scheduleId: Long,
                alarmId: Long,
            ) = "preparationAlarm/$scheduleId/$alarmId"
        }
    }

    @Serializable
    data class DepartureAlarm(
        val scheduleId: Long,
        val alarmId: Long,
    ) : NavRoutes("departureAlarm/{scheduleId}/{alarmId}") {
        companion object {
            const val ROUTE: String = "departureAlarm/{scheduleId}/{alarmId}"

            fun createRoute(
                scheduleId: Long,
                alarmId: Long,
            ) = "departureAlarm/$scheduleId/$alarmId"
        }
    }

    // Splash
    data object SplashGraph : NavRoutes("splashGraph")

    data object Splash : NavRoutes("splash")

    // Login
    data object LoginGraph : NavRoutes("loginGraph")

    data object Login : NavRoutes("login")

    // Onboarding
    data object OnboardingGraph : NavRoutes("onboardingGraph")

    data object Onboarding : NavRoutes("onboarding")

    // Onboarding Mvi
    data object OnboardingMviGraph : NavRoutes("onboardingMviGraph")

    data object PreparationTime : NavRoutes("preparationTime")

    data object HomeAddress : NavRoutes("homeAddress")

    data object AlarmSound : NavRoutes("alarmSound")

    data object MapProvider : NavRoutes("mapProvider")

    // Main
    data object MainGraph : NavRoutes("mainGraph")

    data object Main : NavRoutes("main")

    data object Home : NavRoutes("home")

    data object Setting : NavRoutes("setting")

    // General
    data object GeneralScheduleGraph : NavRoutes("generalScheduleGraph")

    data object ScheduleRepeatSetting : NavRoutes("scheduleRepeatSetting")

    data object PlacePicker : NavRoutes("placePicker")

    data object RouteLoading : NavRoutes("routeLoading")

    data object CheckSchedule : NavRoutes("checkSchedule")

    // General Mvi
    data object GeneralScheduleMviGraph : NavRoutes("generalScheduleMviGraph")

    data object ScheduleRepeatSettingMvi : NavRoutes("scheduleRepeatSettingMvi")

    data object PlacePickerMvi : NavRoutes("placePickerMvi")

    data object RouteLoadingMvi : NavRoutes("routeLoadingMvi")

    data object CheckScheduleMvi : NavRoutes("checkScheduleMvi")

    // EditSchedule
    @Serializable
    data object EditScheduleGraph : NavRoutes("editScheduleGraph")

    @Serializable
    data class EditSchedule(
        val scheduleId: Long,
    ) : NavRoutes("editSchedule/{scheduleId}") {
        companion object {
            const val ROUTE: String = "editSchedule/{scheduleId}"

            fun createRoute(id: Long) = "editSchedule/$id"
        }
    }

    data object EditPlacePicker : NavRoutes("editPlacePicker")

    data object EditRouteLoading : NavRoutes("editRouteLoading")

    // DeleteAccount
    data object DeleteAccountGraph : NavRoutes("deleteAccountGraph")

    data object DeleteAccount : NavRoutes("deleteAccount")

    // ServiceTerms
    @Serializable
    data object ServiceTermsGraph : NavRoutes("serviceTermsGraph")

    @Serializable
    data class ServiceTerms(
        val isNotification: Boolean,
    ) : NavRoutes("serviceTerms/{isNotification}") {
        companion object {
            const val ROUTE: String = "serviceTerms/{isNotification}"

            fun createRoute(isNotification: Boolean) = "serviceTerms/$isNotification"
        }
    }

    // HomeAddressSetting
    data object HomeAddressSettingGraph : NavRoutes("homeAddressSettingGraph")

    data object HomeAddressSetting : NavRoutes("homeAddressSetting")

    data object HomeAddressEdit : NavRoutes("homeAddressEdit")

    // NavMapSetting
    data object NavMapSettingGraph : NavRoutes("navMapSettingGraph")

    data object NavMapSetting : NavRoutes("navMapSetting")

    // PreparationTimeSetting
    data object PreparationTimeSettingGraph : NavRoutes("preparationTimeSettingGraph")

    data object PreparationTimeEdit : NavRoutes("preparationTimeEdit")

    // Everytime
    data object EverytimeGraph : NavRoutes("everytimeGraph")

    data object Landing : NavRoutes("landing")

    data object UrlInput : NavRoutes("urlInput")

    data object Timetable : NavRoutes("timetable")

    data object EverytimePlacePicker : NavRoutes("everytimePlacePicker")

    data object EverytimeRouteLoading : NavRoutes("everytimeRouteLoading")

    // Calendar
    data object CalendarGraph : NavRoutes("calendarGraph")

    data object Calendar : NavRoutes("calendar")
}
