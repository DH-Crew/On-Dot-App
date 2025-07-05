package com.dh.ondot

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dh.ondot.core.navigation.NavRoutes
import com.dh.ondot.core.navigation.loginNavGraph
import com.dh.ondot.core.navigation.onboardingNavGraph
import com.dh.ondot.core.navigation.splashNavGraph
import com.dh.ondot.core.ui.util.DismissKeyboardOnClick
import com.dh.ondot.presentation.ui.theme.OnDotTheme

@Composable
fun App() {
    val navController = rememberNavController()

    OnDotTheme {
        DismissKeyboardOnClick {
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
                splashNavGraph(navController = navController)
                loginNavGraph(navController = navController)
                onboardingNavGraph(navController = navController)
            }
        }
    }
}