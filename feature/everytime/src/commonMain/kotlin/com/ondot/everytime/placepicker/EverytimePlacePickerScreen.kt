package com.ondot.everytime.placepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.everytime.contract.EverytimeIntent
import com.ondot.everytime.contract.EverytimeSideEffect
import com.ondot.everytime.contract.EverytimeViewModel
import com.ondot.ui.screen.placepicker.PlacePickerScreen
import com.ondot.ui.util.ToastManager
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EverytimePlacePickerRoute(
    viewModel: EverytimeViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToRouteLoading: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val departureFocusRequester = remember { FocusRequester() }
    val arrivalFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var requestedInitialFocus by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                EverytimeSideEffect.RequestArrivalFocus -> arrivalFocusRequester.requestFocus()
                is EverytimeSideEffect.ShowToast -> {
                    ToastManager.show(message = effect.message, type = effect.toastType)
                }
                EverytimeSideEffect.NavigateToRouteLoading -> navigateToRouteLoading()
                else -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!requestedInitialFocus) {
            departureFocusRequester.requestFocus()
            requestedInitialFocus = true
        }

        viewModel.dispatch(EverytimeIntent.InitPlaceHistory)
    }

    PlacePickerScreen(
        state = uiState.placePickerState,
        isEverytime = true,
        buttonEnabled =
            uiState.placePickerState.selectedDeparturePlace != null &&
                uiState.placePickerState.selectedArrivalPlace != null,
        departureFocusRequester = departureFocusRequester,
        arrivalFocusRequester = arrivalFocusRequester,
        onRouteInputChanged = { viewModel.dispatch(EverytimeIntent.UpdateRouteInput(it)) },
        onRouteInputFocused = { viewModel.dispatch(EverytimeIntent.SetFocusedRouterType(it)) },
        onPlaceSelected = {
            viewModel.dispatch(EverytimeIntent.SetSelectedPlace(it))
            focusManager.clearFocus()
        },
        onHistorySelected = {
            viewModel.dispatch(EverytimeIntent.SelectHistory(it))
            focusManager.clearFocus()
        },
        onDeleteHistory = { viewModel.dispatch(EverytimeIntent.DeleteHistory(it)) },
        onToggleCheckBox = { viewModel.dispatch(EverytimeIntent.ToggleCheckBox) },
        onNext = { viewModel.dispatch(EverytimeIntent.CreateSchedule) },
        popScreen = popScreen,
    )
}
