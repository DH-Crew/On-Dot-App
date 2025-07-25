package com.dh.ondot.presentation.general.repeat

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.general.GeneralScheduleEvent
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.repeat.components.DateSettingSection
import com.dh.ondot.presentation.general.repeat.components.RepeatSettingSection
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.StepProgressIndicator
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SCHEDULE_REPEAT_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun ScheduleRepeatSettingScreen(
    viewModel: GeneralScheduleViewModel,
    navigateToMain: () -> Unit,
    navigateToPlacePicker: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) {
            viewModel.initStep()
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is GeneralScheduleEvent.NavigateToPlacePicker -> navigateToPlacePicker()
            }
        }
    }

    ScheduleRepeatSettingContent(
        uiState = uiState,
        interactionSource = interactionSource,
        scrollState = scrollState,
        isButtonEnabled = viewModel.isButtonEnabled(),
        onClickSwitch = viewModel::onClickSwitch,
        onClickCheckTextChip = viewModel::onClickCheckTextChip,
        onClickTextChip = viewModel::onClickTextChip,
        onToggleCalendar = viewModel::onToggleCalendar,
        onToggleDial = viewModel::onToggleDial,
        onPrevMonth = viewModel::onPrevMonth,
        onNextMonth = viewModel::onNextMonth,
        onDateSelected = viewModel::onSelectDate,
        onTimeSelected = viewModel::onTimeSelected,
        navigateToMain = navigateToMain,
        onClickButton = viewModel::onClickNextButton
    )
}

@Composable
fun ScheduleRepeatSettingContent(
    uiState: GeneralScheduleUiState,
    interactionSource: MutableInteractionSource,
    scrollState: ScrollState,
    isButtonEnabled: Boolean = false,
    onClickSwitch: (Boolean) -> Unit,
    onClickCheckTextChip: (Int) -> Unit,
    onClickTextChip: (Int) -> Unit,
    onToggleCalendar: () -> Unit,
    onToggleDial: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    navigateToMain: () -> Unit,
    onClickButton: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
    ) {
        TopBar(type = TopBarType.CLOSE, onClick = navigateToMain)

        Spacer(modifier = Modifier.height(24.dp))

        StepProgressIndicator(totalStep = uiState.totalStep, currentStep = uiState.currentStep)

        Spacer(modifier = Modifier.height(34.dp))

        OnDotText(
            text = SCHEDULE_REPEAT_TITLE,
            style = OnDotTextStyle.TitleMediumM,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                RepeatSettingSection(
                    isRepeat = uiState.isRepeat,
                    activeCheckChip = uiState.activeCheckChip,
                    activeWeekDays = uiState.activeWeekDays,
                    onClickSwitch = onClickSwitch,
                    onClickCheckTextChip = onClickCheckTextChip,
                    onClickTextChip = onClickTextChip
                )

                Spacer(modifier = Modifier.height(20.dp))

                DateSettingSection(
                    uiState = uiState,
                    interactionSource = interactionSource,
                    onToggleCalendar = onToggleCalendar,
                    onToggleDial = onToggleDial,
                    onPrevMonth = onPrevMonth,
                    onNextMonth = onNextMonth,
                    onDateSelected = onDateSelected,
                    onTimeSelected = onTimeSelected
                )
            }
        }

        OnDotButton(
            buttonText = WORD_NEXT,
            buttonType = if (isButtonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            onClick = { if (isButtonEnabled) onClickButton() }
        )

        Spacer(modifier = Modifier.height(if (getPlatform().name == ANDROID) 16.dp else 37.dp))
    }
}

