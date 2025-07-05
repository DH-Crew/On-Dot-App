package com.dh.ondot.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.onboarding.step.OnboardingStep1
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.StepProgressIndicator
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.WORD_NEXT

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel { OnboardingViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) viewModel.initStep()
    }

    OnboardingContent(
        uiState = uiState,
        isButtonEnabled = viewModel.isButtonEnabled(),
        onClickNext = { viewModel.onClickNext() },
        onHourInputChanged = { viewModel.onHourInputChanged(it) },
        onMinuteInputChanged = { viewModel.onMinuteInputChanged(it) }
    )
}

@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    isButtonEnabled: Boolean = false,
    onClickNext: () -> Unit,
    onHourInputChanged: (String) -> Unit,
    onMinuteInputChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnDotColor.Gray900)
            .padding(horizontal = 22.dp)
    ) {
        TopBar()

        Spacer(modifier = Modifier.height(24.dp))

        StepProgressIndicator(totalStep = uiState.totalStep, currentStep = uiState.currentStep)

        Spacer(modifier = Modifier.height(34.dp))

        when (uiState.currentStep) {
            1 -> OnboardingStep1(
                hourInput = uiState.hourInput,
                minuteInput = uiState.minuteInput,
                onHourInputChanged = onHourInputChanged,
                onMinuteInputChanged = onMinuteInputChanged
            )
            2 -> TODO("OnboardingStep2()")
            3 -> TODO("OnboardingStep3()")
            4 -> TODO("OnboardingStep4()")
            5 -> TODO("OnboardingStep5()")
        }

        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = WORD_NEXT,
            buttonType = if (isButtonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            onClick = {
                if (isButtonEnabled) {
                    onClickNext()
                }
            }
        )

        Spacer(modifier = Modifier.height(if (getPlatform().name == ANDROID) 16.dp else 37.dp))
    }
}