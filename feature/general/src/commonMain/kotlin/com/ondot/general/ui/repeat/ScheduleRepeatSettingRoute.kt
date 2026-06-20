package com.ondot.general.ui.repeat

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.general.contract.GeneralScheduleIntent
import com.ondot.general.contract.GeneralScheduleSideEffect
import com.ondot.general.contract.GeneralScheduleViewModel
import com.ondot.general.contract.toLegacyUiState
import com.ondot.general.repeat.ScheduleRepeatSettingContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScheduleRepeatSettingRoute(
    viewModel: GeneralScheduleViewModel = koinViewModel(),
    navigateToMain: () -> Unit,
    navigateToPlacePicker: () -> Unit,
    popScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) {
            viewModel.dispatch(GeneralScheduleIntent.InitStep)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                GeneralScheduleSideEffect.NavigateToPlacePicker -> navigateToPlacePicker()
                else -> Unit
            }
        }
    }

    LaunchedEffect(uiState.isActiveDial) {
        if (uiState.isActiveDial) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    ScheduleRepeatSettingContent(
        uiState = uiState.toLegacyUiState(),
        scrollState = scrollState,
        isButtonEnabled = uiState.isRepeatStepButtonEnabled,
        onClickSwitch = { viewModel.dispatch(GeneralScheduleIntent.ToggleRepeat(it)) },
        onClickCheckTextChip = { viewModel.dispatch(GeneralScheduleIntent.SelectRepeatPreset(it)) },
        onClickTextChip = { viewModel.dispatch(GeneralScheduleIntent.ToggleWeekDay(it)) },
        onToggleCalendar = { viewModel.dispatch(GeneralScheduleIntent.ToggleCalendar) },
        onToggleDial = { viewModel.dispatch(GeneralScheduleIntent.ToggleTimeDial) },
        onPrevMonth = { viewModel.dispatch(GeneralScheduleIntent.MoveToPreviousMonth) },
        onNextMonth = { viewModel.dispatch(GeneralScheduleIntent.MoveToNextMonth) },
        onDateSelected = { viewModel.dispatch(GeneralScheduleIntent.SelectDate(it)) },
        onTimeSelected = { viewModel.dispatch(GeneralScheduleIntent.SelectTime(it)) },
        navigateToMain = navigateToMain,
        onClickButton = { viewModel.dispatch(GeneralScheduleIntent.ClickNext) },
        onBack = popScreen,
    )
}
