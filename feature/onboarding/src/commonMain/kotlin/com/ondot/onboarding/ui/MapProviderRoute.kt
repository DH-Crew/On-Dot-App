package com.ondot.onboarding.ui

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_NONE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_STUDENT
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_TITLE1
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_TITLE1_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_TITLE2
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_TITLE2_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_USER_TYPE_WORKER
import com.dh.ondot.presentation.ui.theme.WORD_CONFIRM
import com.ondot.designsystem.components.CommonBottomSheet
import com.ondot.designsystem.components.MapProviderList
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.StepProgressIndicator
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.theme.OnDotColor.GradientHorizontalDivider
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.onboarding.contract.OnboardingIntent
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.ui.platform
import com.ondot.ui.util.buttonPadding
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_bus
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

private enum class NavigationType {
    EveryTime,
    GeneralSchedule,
    Home,
}

@Composable
fun MapProviderRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToEverytime: () -> Unit,
    navigateToGeneralSchedule: () -> Unit,
    navigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MapProviderScreen(
        totalStep = uiState.totalStep,
        currentStep = uiState.currentStep,
        selectedProvider = uiState.selectedMapProvider,
        onBack = popScreen,
        onProviderClick = { viewModel.dispatch(OnboardingIntent.SetMapProvider(it)) },
        onComplete = {
            when (it) {
                NavigationType.EveryTime -> navigateToEverytime()
                NavigationType.GeneralSchedule -> navigateToGeneralSchedule()
                NavigationType.Home -> navigateToHome()
            }
        },
    )
}

@Composable
private fun MapProviderScreen(
    totalStep: Int = 2,
    currentStep: Int = 1,
    selectedProvider: MapProvider = MapProvider.NAVER,
    onBack: () -> Unit = {},
    onProviderClick: (MapProvider) -> Unit = {},
    onComplete: (NavigationType) -> Unit = {},
) {
    var showUserTypeBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetTransitionState = remember { MutableTransitionState(false) }
    val mapProviders =
        if (platform() == ANDROID) {
            listOf(
                MapProvider.KAKAO,
                MapProvider.NAVER,
            )
        } else {
            listOf(
                MapProvider.KAKAO,
                MapProvider.NAVER,
                MapProvider.APPLE,
            )
        }

    LaunchedEffect(showUserTypeBottomSheet) {
        bottomSheetTransitionState.targetState = showUserTypeBottomSheet
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                    .buttonPadding(),
        ) {
            CommonTopBar(
                style = TopBarStyle.BackCenterTitle(""),
                onClick = onBack,
            )

            Spacer(Modifier.height(24.dp))

            StepProgressIndicator(
                totalStep = totalStep,
                currentStep = currentStep,
            )

            Spacer(Modifier.height(32.dp))

            OnDotHighlightText(
                text = ONBOARDING4_TITLE,
                highlight = ONBOARDING4_TITLE_HIGHLIGHT,
                style = OnDotTextStyle.TitleMediumM,
            )

            Spacer(Modifier.height(8.dp))

            OnDotText(
                text = ONBOARDING4_SUB_TITLE,
                style = OnDotTextStyle.BodyMediumR,
                color = Gray300,
            )

            Spacer(Modifier.height(40.dp))

            MapProviderList(
                modifier = Modifier.weight(1f),
                mapProviders = mapProviders,
                selectedProvider = selectedProvider,
                onProviderClick = onProviderClick,
            )

            Spacer(Modifier.height(16.dp))

            OnDotButton(
                buttonText = WORD_CONFIRM,
                buttonType = ButtonType.Green500,
                onClick = {
                    showUserTypeBottomSheet = true
                },
            )
        }

        CommonBottomSheet(
            visibleState = bottomSheetTransitionState,
            onDismiss = { showUserTypeBottomSheet = false },
        ) {
            UserTypeBottomSheetContent(
                onClick = {
                    showUserTypeBottomSheet = false
                    onComplete(it)
                },
            )
        }
    }
}

@Composable
private fun UserTypeBottomSheetContent(onClick: (NavigationType) -> Unit = {}) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OnDotHighlightText(
                text = ONBOARDING4_USER_TYPE_TITLE1,
                highlight = ONBOARDING4_USER_TYPE_TITLE1_HIGHLIGHT,
                style = OnDotTextStyle.TitleSmallSB,
            )
            Spacer(Modifier.width(4.dp))
            OnDotHighlightText(
                text = ONBOARDING4_USER_TYPE_TITLE2,
                highlight = ONBOARDING4_USER_TYPE_TITLE2_HIGHLIGHT,
                style = OnDotTextStyle.TitleSmallSB,
            )
        }
        Spacer(Modifier.height(8.dp))
        OnDotText(
            text = ONBOARDING4_USER_TYPE_SUB_TITLE,
            color = Gray200,
            style = OnDotTextStyle.BodyMediumR,
        )
        Spacer(Modifier.height(64.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_bus),
                contentDescription = null,
                modifier = Modifier.size(width = 189.dp, height = 66.dp),
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .width(349.dp)
                        .height(6.dp)
                        .background(GradientHorizontalDivider),
            )
        }
        Spacer(Modifier.height(36.dp))
        UserTypeButtons(onClick = onClick)
    }
}

@Composable
private fun UserTypeButtons(onClick: (NavigationType) -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            OnDotButton(
                modifier = Modifier.weight(1f),
                buttonText = ONBOARDING4_USER_TYPE_WORKER,
                buttonType = ButtonType.Gray400,
                onClick = { onClick(NavigationType.GeneralSchedule) },
            )
            Spacer(Modifier.width(12.dp))
            OnDotButton(
                modifier = Modifier.weight(1f),
                buttonText = ONBOARDING4_USER_TYPE_STUDENT,
                buttonType = ButtonType.Gray400,
                onClick = { onClick(NavigationType.EveryTime) },
            )
        }
        Spacer(Modifier.height(12.dp))
        OnDotText(
            text = ONBOARDING4_USER_TYPE_NONE,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray400,
            textDecoration = TextDecoration.Underline,
            modifier =
                Modifier
                    .clickable { onClick(NavigationType.Home) },
        )
    }
}
