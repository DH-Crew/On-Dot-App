package com.ondot.everytime

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.LANDING_SCREEN_TITLE
import com.dh.ondot.presentation.ui.theme.START_INTEGRATION
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.TopBar
import com.ondot.designsystem.getPlatform
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.everytime.component.AutoAlarmSection
import com.ondot.everytime.component.BenefitsSection
import com.ondot.everytime.component.HeroSection
import com.ondot.everytime.component.TrackableSection

@Composable
fun LandingRoute(
    popScreen: () -> Unit,
    navigateToUrlInput: () -> Unit,
) {
    LandingScreen(
        onBack = popScreen,
        onIntegrationClick = navigateToUrlInput,
    )
}

@Composable
private fun LandingScreen(
    onBack: () -> Unit,
    onIntegrationClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val visibleMap = remember { mutableStateMapOf<String, Boolean>() }
    val density = LocalDensity.current
    val minVisiblePx = remember(density) { with(density) { 40.dp.roundToPx() } }

    // 실제 viewport bounds를 window 기준으로 저장
    var viewportRect by remember { mutableStateOf<Rect?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Gray900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LandingTopBar(onBack = onBack)

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    // 이 Box가 viewport임. window 기준 bounds 확보
                    .onGloballyPositioned { viewportRect = it.boundsInWindow() },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TrackableSection(
                    id = "hero",
                    order = 0,
                    viewportRect = viewportRect,
                    minVisiblePx = minVisiblePx,
                    visibleMap = visibleMap,
                ) { HeroSection() }

                TrackableSection(
                    id = "auto",
                    order = 1,
                    viewportRect = viewportRect,
                    minVisiblePx = minVisiblePx,
                    visibleMap = visibleMap,
                ) { AutoAlarmSection() }

                TrackableSection(
                    id = "benefit",
                    order = 2,
                    viewportRect = viewportRect,
                    minVisiblePx = minVisiblePx,
                    visibleMap = visibleMap,
                ) { BenefitsSection() }

                // 마지막 버튼은 스크롤 하단 도달 시 렌더링
                val showCta = scrollState.value > (scrollState.maxValue - with(density) { 80.dp.roundToPx() })
                val alpha by animateFloatAsState(if (showCta) 1f else 0f, tween(500))
                val offsetY by animateDpAsState(if (showCta) 0.dp else 40.dp, tween(500))

                OnDotButton(
                    buttonText = START_INTEGRATION,
                    buttonType = ButtonType.Green500,
                    modifier =
                        Modifier
                            .padding(horizontal = 22.dp)
                            .padding(top = 48.dp)
                            .padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp)
                            .graphicsLayer {
                                this.alpha = alpha
                                translationY = offsetY.toPx()
                            },
                    onClick = onIntegrationClick,
                )
            }
        }
    }
}

@Composable
private fun LandingTopBar(onBack: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Gray900)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        TopBar(
            type = TopBarType.BACK,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
            onClick = onBack,
        )

        OnDotText(
            text = LANDING_SCREEN_TITLE,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp),
        )
    }
}
