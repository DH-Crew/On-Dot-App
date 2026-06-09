package com.ondot.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.platform.util.BackPressHandler
import com.ondot.ui.screen.placepicker.PlacePickerScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditPlacePickerRoute(
    viewModel: EditScheduleViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToRouteLoading: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val departureFocusRequester = remember { FocusRequester() }
    val arrivalFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.preparePlacePicker()

        if (uiState.placePickerState.homeAddress.title
                .isBlank() &&
            !uiState.isHomeAddressInitialized
        ) {
            viewModel.fetchHomeAddress()
        }
        viewModel.fetchPlaceHistory()
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                EditScheduleEvent.RequestArrivalFocus -> arrivalFocusRequester.requestFocus()
                else -> Unit
            }
        }
    }

    LaunchedEffect(uiState.isInitialPlacePicker) {
        if (uiState.isInitialPlacePicker) {
            departureFocusRequester.requestFocus()
            viewModel.setInitialPlacePicker(false)
        }
    }

    BackPressHandler(onBack = popScreen)

    PlacePickerScreen(
        state = uiState.placePickerState,
        buttonEnabled = uiState.isPlacePickerButtonEnabled,
        departureFocusRequester = departureFocusRequester,
        arrivalFocusRequester = arrivalFocusRequester,
        onRouteInputChanged = viewModel::updateRouteInput,
        onRouteInputChangedByType = viewModel::updateRouteInput,
        onRouteInputFocused = viewModel::setFocusedRouterType,
        onPlaceSelected = {
            viewModel.selectPlace(it)
            focusManager.clearFocus()
        },
        onHistorySelected = {
            viewModel.selectHistory(it)
            focusManager.clearFocus()
        },
        onDeleteHistory = viewModel::deletePlaceHistory,
        onToggleCheckBox = viewModel::toggleHomeDeparture,
        onTransportTypeChanged = viewModel::updateTransportType,
        onNext = {
            if (viewModel.applyRouteChangesAndFetchAlarms()) {
                navigateToRouteLoading()
            } else {
                popScreen()
            }
        },
        popScreen = popScreen,
    )
}
