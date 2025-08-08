package com.dh.ondot.presentation.general.loading

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.ROUTE_CALCULATE_LABEL
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res

@Composable
fun RouteLoadingScreen(
    navigateToCheckSchedule: () -> Unit,
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/time_calculate.json").decodeToString())
    }
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = Compottie.IterateForever
    )

    LaunchedEffect(Unit) {
        delay(2000L)
        navigateToCheckSchedule()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberLottiePainter(
                composition = composition,
                progress = { progress }
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        OnDotText(
            text = ROUTE_CALCULATE_LABEL,
            style = OnDotTextStyle.BodyLargeSB,
            color = Gray0,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 230.dp)
        )
    }
}