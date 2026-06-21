package com.ondot.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.dh.ondot.presentation.ui.theme.WORD_MUTE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotRadioButton
import com.ondot.designsystem.components.OnDotSlider
import com.ondot.designsystem.components.OnDotSwitch
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.StepProgressIndicator
import com.ondot.designsystem.components.topbar.CommonTopBar
import com.ondot.designsystem.components.topbar.model.TopBarStyle
import com.ondot.designsystem.getPlatform
import com.ondot.designsystem.theme.OnDotColor
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.ui.AlarmSound
import com.ondot.onboarding.contract.OnboardingIntent
import com.ondot.onboarding.contract.OnboardingUiState
import com.ondot.onboarding.contract.OnboardingViewModel
import com.ondot.ui.util.buttonPadding
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_sound
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlarmSoundRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    navigateToMapProvider: () -> Unit,
    popScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(
            OnboardingIntent.InitStep(
                currentStep = 3,
                totalStep = if (getPlatform() == ANDROID) 4 else 3,
            ),
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.dispatch(OnboardingIntent.StopSound)
        }
    }

    AlarmSoundScreen(
        uiState = uiState,
        onToggleMute = { viewModel.dispatch(OnboardingIntent.SetMute(it)) },
        onCategorySelected = { viewModel.dispatch(OnboardingIntent.SetSoundCategory(it)) },
        onSelectSound = { viewModel.dispatch(OnboardingIntent.SetAlarmSound(it)) },
        onVolumeChange = { viewModel.dispatch(OnboardingIntent.SetVolume(it)) },
        onBack = {
            viewModel.dispatch(OnboardingIntent.StopSound)
            popScreen()
        },
        onNext = {
            viewModel.dispatch(OnboardingIntent.StopSound)
            navigateToMapProvider()
        },
    )
}

@Composable
private fun AlarmSoundScreen(
    uiState: OnboardingUiState = OnboardingUiState(),
    onToggleMute: (Boolean) -> Unit = {},
    onCategorySelected: (Int) -> Unit = {},
    onSelectSound: (String) -> Unit = {},
    onVolumeChange: (Float) -> Unit = {},
    onBack: () -> Unit = {},
    onNext: () -> Unit = {},
) {
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
                totalStep = uiState.totalStep,
                currentStep = uiState.currentStep,
            )

            Spacer(Modifier.height(32.dp))

            SoundSettingContent(
                modifier = Modifier.weight(1f),
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

            Spacer(Modifier.height(16.dp))

            OnDotButton(
                buttonText = WORD_NEXT,
                buttonType = if (uiState.alarmSoundEnabled) ButtonType.Green500 else ButtonType.Gray300,
                enabled = uiState.alarmSoundEnabled,
                onClick = onNext,
            )
        }
    }
}

@Composable
private fun SoundSettingContent(
    isMuted: Boolean,
    categories: List<String>,
    selectedCategoryIndex: Int,
    filteredSounds: List<AlarmSound>,
    selectedSoundId: String?,
    volume: Float,
    onToggleMute: (Boolean) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSelectSound: (String) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        OnDotHighlightText(
            text = ONBOARDING3_TITLE,
            highlight = ONBOARDING3_TITLE_HIGHLIGHT,
            style = OnDotTextStyle.TitleMediumM,
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(
            text = ONBOARDING3_SUB_TITLE,
            style = OnDotTextStyle.BodyMediumR,
            color = OnDotColor.Gray300,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.verticalScroll(scrollState),
        ) {
            MuteSection(isMuted, onToggleMute)

            Spacer(modifier = Modifier.height(20.dp))

            CategorySection(
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onCategorySelected = onCategorySelected,
                interactionSource = interactionSource,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    SoundListSection(
                        sounds = filteredSounds,
                        selectedSoundId = selectedSoundId,
                        onSelectSound = onSelectSound,
                        interactionSource = interactionSource,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    VolumeSection(
                        volume = volume,
                        onVolumeChange = onVolumeChange,
                    )
                }

                if (isMuted) {
                    Box(
                        modifier =
                            Modifier
                                .matchParentSize()
                                .background(Gray900.copy(alpha = 0.7f))
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent(PointerEventPass.Initial)
                                        }
                                    }
                                },
                    )
                }
            }
        }
    }
}

@Composable
private fun MuteSection(
    isMuted: Boolean,
    onToggleMute: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Gray700, RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OnDotText(WORD_MUTE, style = OnDotTextStyle.BodyLargeR1, color = Gray0)
        Spacer(Modifier.weight(1f))
        OnDotSwitch(checked = isMuted, onClick = { onToggleMute(!isMuted) })
    }
}

@Composable
private fun CategorySection(
    categories: List<String>,
    selectedIndex: Int,
    interactionSource: MutableInteractionSource,
    onCategorySelected: (Int) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier =
                    Modifier
                        .background(color = Gray700, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.5.dp, vertical = 10.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = { onCategorySelected(index) },
                        ),
            ) {
                OnDotText(
                    text = title,
                    style = OnDotTextStyle.BodyMediumM,
                    color = if (selected) OnDotColor.Green500 else Gray0,
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun SoundListSection(
    sounds: List<AlarmSound>,
    selectedSoundId: String?,
    interactionSource: MutableInteractionSource,
    onSelectSound: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Gray700, RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp),
    ) {
        sounds.forEach { sound ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                onSelectSound(sound.id)
                            },
                        ).padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnDotRadioButton(selected = sound.id == selectedSoundId)
                Spacer(Modifier.width(8.dp))
                OnDotText(
                    text = sound.label,
                    style = OnDotTextStyle.BodyMediumR,
                    color = Gray0,
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun VolumeSection(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Gray700, RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_sound),
            contentDescription = "볼륨 아이콘",
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(8.dp))
        OnDotSlider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    OnDotTheme {
        AlarmSoundScreen(
            uiState =
                OnboardingUiState(
                    currentStep = 3,
                    totalStep = 4,
                ),
        )
    }
}
