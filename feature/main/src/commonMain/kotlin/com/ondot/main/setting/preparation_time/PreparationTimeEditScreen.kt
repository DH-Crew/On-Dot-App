package com.ondot.main.setting.preparation_time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.ERROR_INVALID_MINUTE_INPUT
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green300
import com.dh.ondot.presentation.ui.theme.SETTING_PREPARE_TIME
import com.dh.ondot.presentation.ui.theme.WORD_SAVE
import com.ondot.design_system.components.HourMinuteTextField
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.TopBar
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.main.setting.SettingEvent
import com.ondot.main.setting.SettingViewModel
import com.ondot.util.AnalyticsLogger
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PreparationTimeEditScreen(
    popScreen: () -> Unit,
    viewModel: SettingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("screen_view_preparation_time_edit")
    }

    PreparationTimeEditContent(
        hourInput = uiState.hourInput,
        minuteInput = uiState.minuteInput,
        buttonEnabled = viewModel.isPreparationTimeEditable(),
        onHourInputChanged = viewModel::onHourInputChanged,
        onMinuteInputChanged = viewModel::onMinuteInputChanged,
        onSaveClick = viewModel::updatePreparationTime,
        popScreen = popScreen
    )

    LaunchedEffect(Unit) {
        viewModel.getPreparationTime()
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when (it) {
                is SettingEvent.PopScreen -> popScreen()
            }
        }
    }
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
    val showError by remember(minuteInput) {
        derivedStateOf {
            val m = minuteInput.toIntOrNull()
            m != null && m > 59
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
            .padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        if (showError) {
            OnDotText(
                text = ERROR_INVALID_MINUTE_INPUT,
                style = OnDotTextStyle.BodySmallR1,
                color = OnDotColor.Red
            )
        }

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
            modifier = Modifier.padding(top = if (getPlatform() == ANDROID) 50.dp else 70.dp)
        )
    }
}