package com.ondot.general.place

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.general.GeneralScheduleEvent
import com.ondot.general.GeneralScheduleViewModel
import com.ondot.platform.util.BackPressHandler
import com.ondot.ui.screen.placepicker.PlacePickerScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlacePickerRoute(
    viewModel: GeneralScheduleViewModel,
    popScreen: () -> Unit,
    navigateToRouteLoading: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val departureFocusRequester = remember { FocusRequester() }
    val arrivalFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackPressHandler(
        onBack = {
            viewModel.onClickBackButton()
            popScreen()
        },
    )

    LaunchedEffect(Unit) {
        if (uiState.placePickerState.homeAddress.title
                .isBlank() &&
            !uiState.isHomeAddressInitialized
        ) {
            viewModel.initHomeAddress()
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when (it) {
                GeneralScheduleEvent.NavigateToRouteLoading -> navigateToRouteLoading()
                GeneralScheduleEvent.RequestArrivalFocus -> arrivalFocusRequester.requestFocus()
            }
        }
    }

    LaunchedEffect(uiState.isInitialPlacePicker) {
        if (uiState.isInitialPlacePicker) {
            departureFocusRequester.requestFocus()
            viewModel.updateInitialPlacePicker(false)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getPlaceHistory()
    }

    PlacePickerScreen(
        state = uiState.placePickerState,
        buttonEnabled = viewModel.isButtonEnabled(),
        departureFocusRequester = departureFocusRequester,
        arrivalFocusRequester = arrivalFocusRequester,
        onRouteInputChanged = viewModel::onRouteInputChanged,
        onRouteInputFocused = viewModel::onRouteInputFieldFocused,
        onPlaceSelected = {
            viewModel.onPlaceSelected(it)
            focusManager.clearFocus()
        },
        onHistorySelected = {
            viewModel.onHistoryPlaceSelected(it)
            focusManager.clearFocus()
        },
        onDeleteHistory = viewModel::deletePlaceHistory,
        onToggleCheckBox = viewModel::onClickCheckBox,
        onNext = viewModel::onClickNextButton,
        popScreen = {
            viewModel.onClickBackButton()
            popScreen()
        },
    )
}
