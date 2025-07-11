package com.dh.ondot.core.navigation

sealed class NavRoutes(val route: String) {
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

    // General
    data object GeneralScheduleGraph: NavRoutes("generalScheduleGraph")
    data object ScheduleRepeatSetting: NavRoutes("scheduleRepeatSetting")
    data object PlacePicker: NavRoutes("placePicker")
    data object RouteLoading: NavRoutes("routeLoading")
    data object CheckSchedule: NavRoutes("checkSchedule")
}