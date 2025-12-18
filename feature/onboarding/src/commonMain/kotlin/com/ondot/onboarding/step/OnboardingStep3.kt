package com.ondot.onboarding.step

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING3_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.WORD_MUTE
import com.ondot.design_system.components.OnDotHighlightText
import com.ondot.design_system.components.OnDotRadioButton
import com.ondot.design_system.components.OnDotSlider
import com.ondot.design_system.components.OnDotSwitch
import com.ondot.design_system.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.ui.AlarmSound
import com.ondot.util.AnalyticsLogger
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_sound
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingStep3(
    isMuted: Boolean,
    categories: List<String>,
    selectedCategoryIndex: Int,
    filteredSounds: List<AlarmSound>,
    selectedSoundId: String?,
    volume: Float,
    onToggleMute: (Boolean) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onSelectSound: (String) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnDotHighlightText(
            text = ONBOARDING3_TITLE,
            highlight = ONBOARDING3_TITLE_HIGHLIGHT,
            style = OnDotTextStyle.TitleMediumM,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(
            text = ONBOARDING3_SUB_TITLE,
            style = OnDotTextStyle.BodyMediumR,
            color = OnDotColor.Green300
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            MuteSection(isMuted, onToggleMute)

            Spacer(modifier = Modifier.height(20.dp))

            CategorySection(
                categories = categories,
                selectedIndex = selectedCategoryIndex,
                onCategorySelected = onCategorySelected,
                interactionSource = interactionSource
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column {
                    SoundListSection(
                        sounds = filteredSounds,
                        selectedSoundId = selectedSoundId,
                        onSelectSound = onSelectSound,
                        interactionSource = interactionSource
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    VolumeSection(
                        volume = volume,
                        onVolumeChange = onVolumeChange
                    )
                }

                if (isMuted) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(OnDotColor.Gray900.copy(alpha = 0.7f))
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent(PointerEventPass.Initial)
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun MuteSection(
    isMuted: Boolean,
    onToggleMute: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(WORD_MUTE, style = OnDotTextStyle.BodyLargeR1, color = OnDotColor.Gray0)
        Spacer(Modifier.weight(1f))
        OnDotSwitch(checked = isMuted, onClick = { onToggleMute(!isMuted) })
    }
}

@Composable
private fun CategorySection(
    categories: List<String>,
    selectedIndex: Int,
    interactionSource: MutableInteractionSource,
    onCategorySelected: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        categories.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .background(color = Gray700, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.5.dp, vertical = 10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = { onCategorySelected(index) }
                    )
            ) {
                OnDotText(
                    text = title,
                    style = OnDotTextStyle.BodyMediumM,
                    color = if (selected) OnDotColor.Green500 else OnDotColor.Gray0
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
    onSelectSound: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        sounds.forEach { sound ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            onSelectSound(sound.id)
                        }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OnDotRadioButton(selected = sound.id == selectedSoundId)
                Spacer(Modifier.width(8.dp))
                OnDotText(
                    text = sound.label,
                    style = OnDotTextStyle.BodyMediumR,
                    color = OnDotColor.Gray0
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun VolumeSection(
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_sound),
            contentDescription = "볼륨 아이콘",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        OnDotSlider(
            value = volume,
            onValueChange = onVolumeChange,
            modifier = Modifier.weight(1f)
        )
    }
}
