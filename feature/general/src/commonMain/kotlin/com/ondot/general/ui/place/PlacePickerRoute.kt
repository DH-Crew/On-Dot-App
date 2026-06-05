package com.ondot.general.ui.place

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ondot.general.contract.GeneralScheduleIntent
import com.ondot.general.contract.GeneralScheduleSideEffect
import com.ondot.general.contract.GeneralScheduleViewModel
import com.ondot.platform.util.BackPressHandler
import com.ondot.ui.screen.placepicker.PlacePickerScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlacePickerRoute(
    viewModel: GeneralScheduleViewModel = koinViewModel(),
    popScreen: () -> Unit,
    navigateToRouteLoading: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val departureFocusRequester = remember { FocusRequester() }
    val arrivalFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (uiState.placePickerState.homeAddress.title
                .isBlank() &&
            !uiState.isHomeAddressInitialized
        ) {
            viewModel.dispatch(GeneralScheduleIntent.InitHomeAddress)
        }
    }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                GeneralScheduleSideEffect.NavigateToRouteLoading -> navigateToRouteLoading()
                GeneralScheduleSideEffect.RequestArrivalFocus -> arrivalFocusRequester.requestFocus()
                else -> Unit
            }
        }
    }

    LaunchedEffect(uiState.isInitialPlacePicker) {
        if (uiState.isInitialPlacePicker) {
            departureFocusRequester.requestFocus()
            viewModel.dispatch(GeneralScheduleIntent.SetInitialPlacePicker(false))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.dispatch(GeneralScheduleIntent.InitPlaceHistory)
    }

    BackPressHandler(
        onBack = {
            viewModel.dispatch(GeneralScheduleIntent.ClickBack)
            popScreen()
        },
    )

    PlacePickerScreen(
        state = uiState.placePickerState,
        buttonEnabled = uiState.isPlacePickerButtonEnabled,
        departureFocusRequester = departureFocusRequester,
        arrivalFocusRequester = arrivalFocusRequester,
        onRouteInputChanged = { viewModel.dispatch(GeneralScheduleIntent.UpdateRouteInput(it)) },
        onRouteInputFocused = { viewModel.dispatch(GeneralScheduleIntent.SetFocusedRouterType(it)) },
        onPlaceSelected = {
            viewModel.dispatch(GeneralScheduleIntent.SelectPlace(it))
            focusManager.clearFocus()
        },
        onHistorySelected = {
            viewModel.dispatch(GeneralScheduleIntent.SelectHistory(it))
            focusManager.clearFocus()
        },
        onDeleteHistory = { viewModel.dispatch(GeneralScheduleIntent.DeleteHistory(it)) },
        onToggleCheckBox = { viewModel.dispatch(GeneralScheduleIntent.ToggleHomeDeparture) },
        onTransportTypeChanged = { viewModel.dispatch(GeneralScheduleIntent.UpdateTransportType(it)) },
        onNext = { viewModel.dispatch(GeneralScheduleIntent.ClickNext) },
        popScreen = {
            viewModel.dispatch(GeneralScheduleIntent.ClickBack)
            popScreen()
        },
    )
}
