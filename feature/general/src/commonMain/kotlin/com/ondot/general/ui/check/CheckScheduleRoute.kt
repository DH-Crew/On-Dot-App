package com.ondot.general.ui.check

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.general.check.CheckScheduleContent
import com.ondot.general.contract.GeneralScheduleIntent
import com.ondot.general.contract.GeneralScheduleSideEffect
import com.ondot.general.contract.GeneralScheduleViewModel
import com.ondot.general.contract.toLegacyUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckScheduleRoute(
    viewModel: GeneralScheduleViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToMain: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequest = remember { FocusRequester() }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is GeneralScheduleSideEffect.NavigateToMain -> navigateToMain()
                else -> Unit
            }
        }
    }

    CheckScheduleContent(
        uiState = uiState.toLegacyUiState(),
        focusRequester = focusRequest,
        onClickBack = popScreen,
        onCreateSchedule = { isMedicationRequired, preparationNote ->
            viewModel.dispatch(GeneralScheduleIntent.CreateSchedule(isMedicationRequired, preparationNote))
        },
        onValueChanged = { viewModel.dispatch(GeneralScheduleIntent.UpdateScheduleTitle(it)) },
        onToggleSwitch = { viewModel.dispatch(GeneralScheduleIntent.TogglePreparationAlarm) },
        onShowBottomSheet = { viewModel.dispatch(GeneralScheduleIntent.SetBottomSheetVisible(true)) },
        onDismiss = { viewModel.dispatch(GeneralScheduleIntent.SetBottomSheetVisible(false)) },
        onShowAlarmTimeBottomSheet = { viewModel.dispatch(GeneralScheduleIntent.SetAlarmTimeBottomSheet(it)) },
        onDismissAlarmTimeBottomSheet = { viewModel.dispatch(GeneralScheduleIntent.SetAlarmTimeBottomSheet(null)) },
        onEditAlarmTime = { type, date, time ->
            viewModel.dispatch(GeneralScheduleIntent.UpdateAlarmTime(type, date, time))
        },
    )
}
