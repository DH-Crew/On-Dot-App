package com.dh.ondot.core.navigation

sealed class NavRoutes(val route: String) {
    // Splash
    data object SplashGraph: NavRoutes("splashGraph")
    data object Splash: NavRoutes("splash")

    // Login
    data object LoginGraph: NavRoutes("loginGraph")
    data object Login: NavRoutes("login")
}