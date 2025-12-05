package com.ondot.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.StepProgressIndicator
import com.ondot.design_system.components.TopBar
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.onboarding.step.OnboardingStep1
import com.ondot.onboarding.step.OnboardingStep2
import com.ondot.onboarding.step.OnboardingStep3
import com.ondot.platform.util.BackPressHandler
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = koinViewModel(),
    navigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackPressHandler { viewModel.onClickBack() }

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) viewModel.initStep()
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when(it) {
                is OnboardingEvent.NavigateToMainScreen -> navigateToMain()
            }
        }
    }

    OnboardingContent(
        uiState = uiState,
        isButtonEnabled = viewModel.isButtonEnabled(),
        onClickNext = { viewModel.onClickNext() },
        onHourInputChanged = { viewModel.onHourInputChanged(it) },
        onMinuteInputChanged = { viewModel.onMinuteInputChanged(it) },
        onAddressInputChanged = { viewModel.onAddressInputChanged(it) },
        onClickPlace = { viewModel.onClickPlace(it) },
        onToggleMute = { viewModel.onToggleMute(it) },
        onCategorySelected = { viewModel.onCategorySelected(it) },
        onSelectSound = { viewModel.onSelectSound(it) },
        onVolumeChange = { viewModel.onVolumeChange(it) },
        onClickBack = { viewModel.onClickBack() }
    )
}

@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    isButtonEnabled: Boolean = false,
    onClickNext: () -> Unit,
    onHourInputChanged: (String) -> Unit,
    onMinuteInputChanged: (String) -> Unit,
    onAddressInputChanged: (String) -> Unit,
    onClickPlace: (AddressInfo) -> Unit,
    onToggleMute: (Boolean) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSelectSound: (String) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onClickBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnDotColor.Gray900)
    ) {
        TopBar(
            modifier = Modifier.padding(horizontal = 22.dp),
            onClick = onClickBack
        )

        Spacer(modifier = Modifier.height(24.dp))

        StepProgressIndicator(
            totalStep = uiState.totalStep,
            currentStep = uiState.currentStep,
            modifier = Modifier.padding(horizontal = 22.dp)
        )

        Spacer(modifier = Modifier.height(34.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (getPlatform() == ANDROID) {
                when (uiState.currentStep) {
                    1 -> OnboardingStep1(
                        hourInput = uiState.hourInput,
                        minuteInput = uiState.minuteInput,
                        onHourInputChanged = onHourInputChanged,
                        onMinuteInputChanged = onMinuteInputChanged
                    )
                    2 -> OnboardingStep2(
                        addressInput = uiState.addressInput,
                        onAddressInputChanged = onAddressInputChanged,
                        addressList = uiState.addressList,
                        onClickPlace = onClickPlace
                    )
                    3 -> OnboardingStep3(
                        isMuted = uiState.isMuted,
                        categories = uiState.categories,
                        selectedCategoryIndex = uiState.selectedCategoryIndex,
                        filteredSounds = uiState.filteredSounds,
                        selectedSoundId = uiState.selectedSound,
                        volume = uiState.volume,
                        onToggleMute = onToggleMute,
                        onCategorySelected = onCategorySelected,
                        onSelectSound = onSelectSound,
                        onVolumeChange = onVolumeChange,
                    )
                }
            } else {
                when (uiState.currentStep) {
                    1 -> OnboardingStep1(
                        hourInput = uiState.hourInput,
                        minuteInput = uiState.minuteInput,
                        onHourInputChanged = onHourInputChanged,
                        onMinuteInputChanged = onMinuteInputChanged
                    )
                    2 -> OnboardingStep2(
                        addressInput = uiState.addressInput,
                        onAddressInputChanged = onAddressInputChanged,
                        addressList = uiState.addressList,
                        onClickPlace = onClickPlace
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OnDotButton(
            buttonText = WORD_NEXT,
            buttonType = if (isButtonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            modifier = Modifier.padding(horizontal = 22.dp),
            onClick = {
                if (isButtonEnabled) {
                    onClickNext()
                }
            }
        )

        Spacer(modifier = Modifier.height(if (getPlatform() == ANDROID) 16.dp else 37.dp))
    }
}