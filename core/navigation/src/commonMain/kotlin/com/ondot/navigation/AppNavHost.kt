package com.ondot.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ondot.navigation.base.NavGraphContributor
import com.ondot.ui.util.ToastHost
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