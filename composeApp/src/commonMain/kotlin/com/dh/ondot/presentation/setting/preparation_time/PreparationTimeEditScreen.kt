package com.dh.ondot.presentation.setting.preparation_time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.onboarding.step.HourMinuteTextField
import com.dh.ondot.presentation.setting.SettingViewModel
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green300
import com.dh.ondot.presentation.ui.theme.SETTING_PREPARE_TIME
import com.dh.ondot.presentation.ui.theme.WORD_SAVE

@Composable
fun PreparationTimeEditScreen(
    popScreen: () -> Unit,
    viewModel: SettingViewModel = viewModel { SettingViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PreparationTimeEditContent(
        hourInput = uiState.hourInput,
        minuteInput = uiState.minuteInput,
        buttonEnabled = uiState.hourInput.isNotEmpty() || uiState.minuteInput.isNotEmpty(),
        onHourInputChanged = viewModel::onHourInputChanged,
        onMinuteInputChanged = viewModel::onMinuteInputChanged,
        onSaveClick = viewModel::updatePreparationTime,
        popScreen = popScreen
    )
}

@Composable
fun PreparationTimeEditContent(
    hourInput: String,
    minuteInput: String,
    buttonEnabled: Boolean,
    onHourInputChanged: (String) -> Unit,
    onMinuteInputChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    popScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
            .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp)
    ) {
        TopBarSection(onBack = popScreen)

        Spacer(modifier = Modifier.height(32.dp))

        OnDotText(
            text = ONBOARDING1_TITLE,
            textAlign = TextAlign.Start,
            style = OnDotTextStyle.TitleMediumM,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(text = ONBOARDING1_SUB_TITLE, style = OnDotTextStyle.BodyMediumR, color = Green300 )

        Spacer(modifier = Modifier.height(40.dp))

        HourMinuteTextField(
            hourInput = hourInput,
            minuteInput = minuteInput,
            onHourInputChanged = onHourInputChanged,
            onMinuteInputChanged = onMinuteInputChanged
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = WORD_SAVE,
            buttonType = if (buttonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            onClick = {
                if (buttonEnabled) onSaveClick()
            }
        )
    }
}

@Composable
private fun TopBarSection(
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TopBar(
            type = TopBarType.BACK,
            onClick = onBack
        )

        OnDotText(
            text = SETTING_PREPARE_TIME,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0,
            modifier = Modifier.padding(top = if (getPlatform().name == ANDROID) 50.dp else 70.dp)
        )
    }
}