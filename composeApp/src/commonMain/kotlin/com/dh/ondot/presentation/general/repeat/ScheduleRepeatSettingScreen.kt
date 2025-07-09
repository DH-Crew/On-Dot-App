package com.dh.ondot.presentation.general.repeat

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
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.repeat.components.DateSettingSection
import com.dh.ondot.presentation.general.repeat.components.RepeatSettingSection
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.StepProgressIndicator
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SCHEDULE_REPEAT_TITLE
import kotlinx.datetime.LocalDate

@Composable
fun ScheduleRepeatSettingScreen(
    viewModel: GeneralScheduleViewModel,
    navigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) {
            viewModel.initStep()
        }
    }

    ScheduleRepeatSettingContent(
        uiState = uiState,
        onClickSwitch = viewModel::onClickSwitch,
        onClickCheckTextChip = viewModel::onClickCheckTextChip,
        onClickTextChip = viewModel::onClickTextChip,
        onToggleCalendar = viewModel::onToggleCalendar,
        onPrevMonth = viewModel::onPrevMonth,
        onNextMonth = viewModel::onNextMonth,
        onDateSelected = viewModel::onSelectDate,
        navigateToMain = navigateToMain
    )
}

@Composable
fun ScheduleRepeatSettingContent(
    uiState: GeneralScheduleUiState,
    onClickSwitch: (Boolean) -> Unit,
    onClickCheckTextChip: (Int) -> Unit,
    onClickTextChip: (Int) -> Unit,
    onToggleCalendar: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    navigateToMain: () -> Unit,
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
            onToggleCalendar = onToggleCalendar,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth,
            onDateSelected = onDateSelected
        )
    }
}

