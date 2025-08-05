package com.dh.ondot.presentation.general.place

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.core.di.BackPressHandler
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.RouterType
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.general.GeneralScheduleEvent
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.place.components.RouteInputSection
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotCheckBox
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.PlaceSearchResultItem
import com.dh.ondot.presentation.ui.components.StepProgressIndicator
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DEPARTURE_FROM_HOME
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.PLACE_PICKER_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlacePickerScreen(
    viewModel: GeneralScheduleViewModel,
    popScreen: () -> Unit,
    navigateToRouteLoading: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val departureFocusRequester = remember { FocusRequester() }
    val arrivalFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BackPressHandler(
        onBack = {
            viewModel.onClickBackButton()
            popScreen()
        }
    )

    LaunchedEffect(Unit) {
        if (uiState.homeAddress.title.isBlank() && !uiState.isHomeAddressInitialized) {
            viewModel.initHomeAddress()
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when (it) {
                GeneralScheduleEvent.NavigateToRouteLoading -> navigateToRouteLoading()
                GeneralScheduleEvent.ActiveArrivalFocusRequester -> arrivalFocusRequester.requestFocus()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.isInitialPlacePicker) {
            departureFocusRequester.requestFocus()
            viewModel.updateInitialPlacePicker(false)
        }
    }

    PlacePickerContent(
        uiState = uiState,
        buttonEnabled = viewModel.isButtonEnabled(),
        departureFocusRequester = departureFocusRequester,
        arrivalFocusRequester = arrivalFocusRequester,
        onRouteInputChanged = viewModel::onRouteInputChanged,
        onRouteInputFocused = viewModel::onRouteInputFieldFocused,
        onPlaceSelected = {
            viewModel.onPlaceSelected(it)
            focusManager.clearFocus()
        },
        onClickCheckBox = viewModel::onClickCheckBox,
        onClickButton = viewModel::onClickNextButton,
        popScreen = {
            viewModel.onClickBackButton()
            popScreen()
        }
    )
}

@Composable
fun PlacePickerContent(
    uiState: GeneralScheduleUiState,
    buttonEnabled: Boolean = false,
    departureFocusRequester: FocusRequester = remember { FocusRequester() },
    arrivalFocusRequester: FocusRequester = remember { FocusRequester() },
    onRouteInputChanged: (String) -> Unit,
    onRouteInputFocused: (RouterType) -> Unit,
    onPlaceSelected: (AddressInfo) -> Unit,
    onClickCheckBox: () -> Unit,
    onClickButton: () -> Unit,
    popScreen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            TopBar(type = TopBarType.BACK, onClick = popScreen)

            Spacer(modifier = Modifier.height(24.dp))

            StepProgressIndicator(totalStep = uiState.totalStep, currentStep = uiState.currentStep)

            Spacer(modifier = Modifier.height(24.dp))

            OnDotText(
                text = PLACE_PICKER_TITLE,
                style = OnDotTextStyle.TitleMediumM,
                color = Gray0
            )

            Spacer(modifier = Modifier.height(48.dp))

            HomeDepartureOption(
                isChecked = uiState.isChecked,
                onClick = onClickCheckBox
            )

            Spacer(modifier = Modifier.height(20.dp))

            RouteInputSection(
                departurePlaceInput = uiState.departurePlaceInput,
                arrivalPlaceInput = uiState.arrivalPlaceInput,
                departureFocusRequester = departureFocusRequester,
                arrivalFocusRequester = arrivalFocusRequester,
                onRouteInputChanged = onRouteInputChanged,
                onRouteInputFocused = onRouteInputFocused
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Gray800))

        Box(modifier = Modifier.weight(1f)) {
            PlaceList(
                list = uiState.placeList,
                addressInput = when (uiState.lastFocusedTextField) {
                    RouterType.Departure -> uiState.departurePlaceInput
                    RouterType.Arrival -> uiState.arrivalPlaceInput
                },
                onPlaceSelected = onPlaceSelected
            )
        }

        OnDotButton(
            buttonText = WORD_NEXT,
            buttonType = if (buttonEnabled) ButtonType.Green500 else ButtonType.Gray300,
            onClick = { if (buttonEnabled) onClickButton() },
            modifier = Modifier.padding(horizontal = 22.dp)
        )
    }
}

@Composable
fun HomeDepartureOption(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotCheckBox(isChecked = isChecked, onCheckedChange = onClick)

        Spacer(modifier = Modifier.width(8.dp))

        OnDotText(
            text = DEPARTURE_FROM_HOME,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray200
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun PlaceList(
    list: List<AddressInfo>,
    addressInput: String,
    onPlaceSelected: (AddressInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
    ) {
        itemsIndexed(list, key = { _, item -> "${item.latitude}_${item.longitude}_${item.title}" }) { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlaceSelected(item) }
            ) {
                PlaceSearchResultItem(addressInput = addressInput, item = item)
                HorizontalDivider(thickness = (0.5).dp, color = Gray800)
            }
        }
    }
}