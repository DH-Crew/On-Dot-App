package com.ondot.onboarding.ui

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.dh.ondot.presentation.ui.theme.WORD_CONFIRM
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.dh.ondot.presentation.ui.theme.formatPreparationTime
import com.ondot.designsystem.components.CommonBottomSheet
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.StepProgressIndicator
import com.ondot.designsystem.components.TimePicker
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.getPlatform
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray600
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.onboarding.contract.OnboardingIntent
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.ui.util.buttonPadding
import com.ondot.ui.util.noRippleClickable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PreparationTimeRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    navigateToHomeAddress: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(
            OnboardingIntent.InitStep(
                currentStep = 1,
                totalStep = if (getPlatform() == ANDROID) 4 else 3,
            ),
        )
    }

    PreparationTimeScreen(
        currentStep = uiState.currentStep,
        totalStep = uiState.totalStep,
        preparationTime = uiState.preparationTime,
        enabled = uiState.preparationTimeEnabled,
        onPreparationTimeSelected = { viewModel.dispatch(OnboardingIntent.SetPreparationTime(it)) },
        onNext = navigateToHomeAddress,
    )
}

@Composable
private fun PreparationTimeScreen(
    currentStep: Int = 1,
    totalStep: Int = 2,
    preparationTime: Int = 60,
    enabled: Boolean = false,
    onPreparationTimeSelected: (Int) -> Unit = {},
    onNext: () -> Unit = {},
) {
    var showPreparationTimeBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetTransitionState = remember { MutableTransitionState(false) }

    LaunchedEffect(showPreparationTimeBottomSheet) {
        bottomSheetTransitionState.targetState = showPreparationTimeBottomSheet
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
                    .padding(horizontal = 22.dp)
                    .buttonPadding(),
        ) {
            CommonTopBar(style = TopBarStyle.None)

            Spacer(Modifier.height(24.dp))

            StepProgressIndicator(
                totalStep = totalStep,
                currentStep = currentStep,
            )

            Spacer(Modifier.height(32.dp))

            OnDotHighlightText(
                text = ONBOARDING1_TITLE,
                highlight = ONBOARDING1_TITLE_HIGHLIGHT,
                style = OnDotTextStyle.TitleMediumM,
            )

            Spacer(Modifier.height(8.dp))

            OnDotText(
                text = ONBOARDING1_SUB_TITLE,
                style = OnDotTextStyle.BodyMediumR,
                color = Gray300,
            )

            Spacer(Modifier.height(40.dp))

            PreparationTime(
                preparationTime = preparationTime,
                onClick = { showPreparationTimeBottomSheet = true },
            )

            Spacer(Modifier.weight(1f))

            OnDotButton(
                buttonText = WORD_NEXT,
                buttonType = if (enabled) ButtonType.Green500 else ButtonType.Gray300,
                onClick = {
                    if (enabled) onNext()
                },
            )
        }

        PreparationTimeBottomSheet(
            visibleState = bottomSheetTransitionState,
            preparationTime = preparationTime,
            onPreparationTimeSelected = onPreparationTimeSelected,
            onDismiss = { showPreparationTimeBottomSheet = false },
        )
    }
}

@Composable
private fun PreparationTimeBottomSheet(
    visibleState: MutableTransitionState<Boolean>,
    preparationTime: Int,
    onPreparationTimeSelected: (Int) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = preparationTime / 60)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = preparationTime % 60)
    var selectedPreparationTime by remember(preparationTime) {
        mutableStateOf(preparationTime)
    }

    CommonBottomSheet(
        visibleState = visibleState,
        onDismiss = onDismiss,
    ) {
        TimePicker(
            periodState = periodState,
            hourState = hourState,
            minuteState = minuteState,
            minuteStep = 5,
            onTimeSelected = { time ->
                selectedPreparationTime = time.hour * 60 + time.minute
            },
        )

        Spacer(Modifier.height(24.dp))

        OnDotButton(
            buttonText = WORD_CONFIRM,
            buttonType = ButtonType.Green500,
            onClick = {
                onPreparationTimeSelected(selectedPreparationTime)
                onDismiss()
            },
        )
    }
}

@Composable
private fun PreparationTime(
    preparationTime: Int,
    onClick: () -> Unit,
) {
    val preparationTimeText = formatPreparationTime(preparationTime / 60, preparationTime % 60)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(Gray700, RoundedCornerShape(12.dp))
                .border(1.dp, Gray600, RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp)
                .noRippleClickable { onClick() },
        contentAlignment = Alignment.CenterStart,
    ) {
        OnDotText(
            text = preparationTimeText,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    OnDotTheme {
        PreparationTimeScreen()
    }
}
