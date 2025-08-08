package com.dh.ondot.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = viewModel { SplashViewModel() },
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/splash.json").decodeToString())
    }
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = Compottie.IterateForever
    )

    LaunchedEffect(Unit) {
        delay(2000L)
        if (uiState.skipLogin) navigateToHome()
        else navigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff1B1B1B)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberLottiePainter(
                composition = composition,
                progress = { progress }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 92.dp),
            contentScale = ContentScale.Fit
        )
    }
}