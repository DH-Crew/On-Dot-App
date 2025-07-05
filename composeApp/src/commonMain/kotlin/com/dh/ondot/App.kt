package com.dh.ondot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dh.ondot.core.navigation.NavRoutes
import com.dh.ondot.core.navigation.loginNavGraph
import com.dh.ondot.core.navigation.splashNavGraph
import com.dh.ondot.presentation.ui.theme.OnDotTheme

@Composable
fun App() {
    val navController = rememberNavController()

    OnDotTheme {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SplashGraph.route,
            modifier = Modifier.fillMaxSize()
        ) {
            splashNavGraph(navController = navController)
            loginNavGraph(navController = navController)
        }
    }
}