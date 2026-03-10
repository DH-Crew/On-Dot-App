package com.ondot.ui.screen.placepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.DEPARTURE_FROM_HOME
import com.dh.ondot.presentation.ui.theme.EVERYTIME_PLACE_PICKER_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.PLACE_PICKER_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_NEXT
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.OnDotCheckBox
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.components.PlaceHistoryItem
import com.ondot.designsystem.components.PlaceSearchResultItem
import com.ondot.designsystem.components.RouteInputSection
import com.ondot.designsystem.components.StepProgressIndicator
import com.ondot.designsystem.components.TopBar
import com.ondot.designsystem.getPlatform
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.RouterType
import com.ondot.domain.model.enums.TopBarType
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.ui.screen.placepicker.model.PlacePickerUiModel

@Composable
fun PlacePickerScreen(
    state: PlacePickerUiModel,
    isEverytime: Boolean = false,
    buttonEnabled: Boolean = false,
    departureFocusRequester: FocusRequester = remember { FocusRequester() },
    arrivalFocusRequester: FocusRequester = remember { FocusRequester() },
    onRouteInputChanged: (String) -> Unit,
    onRouteInputFocused: (RouterType) -> Unit,
    onPlaceSelected: (AddressInfo) -> Unit,
    onHistorySelected: (PlaceHistory) -> Unit,
    onDeleteHistory: (PlaceHistory) -> Unit,
    onToggleCheckBox: () -> Unit,
    onNext: () -> Unit,
    popScreen: () -> Unit,
) {
    val buttonType =
        if (buttonEnabled) {
            if (isEverytime) {
                ButtonType.Gradient
            } else {
                ButtonType.Green500
            }
        } else {
            ButtonType.Gray300
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp),
        ) {
            TopBar(type = TopBarType.BACK, onClick = popScreen)

            if (state.steps != null) {
                Spacer(modifier = Modifier.height(24.dp))

                StepProgressIndicator(totalStep = state.steps.second, currentStep = state.steps.first)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OnDotText(
                text = if (isEverytime) EVERYTIME_PLACE_PICKER_TITLE else PLACE_PICKER_TITLE,
                style = OnDotTextStyle.TitleMediumM,
                color = Gray0,
            )

            Spacer(modifier = Modifier.height(48.dp))

            HomeDepartureOption(
                isChecked = state.isChecked,
                onClick = onToggleCheckBox,
            )

            Spacer(modifier = Modifier.height(20.dp))

            RouteInputSection(
                departurePlaceInput = state.departurePlaceInput,
                arrivalPlaceInput = state.arrivalPlaceInput,
                departureFocusRequester = departureFocusRequester,
                arrivalFocusRequester = arrivalFocusRequester,
                onRouteInputChanged = onRouteInputChanged,
                onRouteInputFocused = onRouteInputFocused,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Gray800))

        Box(modifier = Modifier.weight(1f)) {
            PlaceList(
                list = state.placeList,
                historyList = state.placeHistory,
                addressInput =
                    when (state.lastFocusedTextField) {
                        RouterType.Departure -> state.departurePlaceInput
                        RouterType.Arrival -> state.arrivalPlaceInput
                    },
                onPlaceSelected = onPlaceSelected,
                onHistoryClose = { onDeleteHistory(state.placeHistory[it]) },
                onHistoryPlaceSelected = onHistorySelected,
            )
        }

        OnDotButton(
            buttonText = if (isEverytime) CREATE_SCHEDULE else WORD_NEXT,
            buttonType = buttonType,
            onClick = { if (buttonEnabled) onNext() },
            modifier = Modifier.padding(horizontal = 22.dp),
        )
    }
}

@Composable
fun HomeDepartureOption(
    isChecked: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier =
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OnDotCheckBox(isChecked = isChecked, onCheckedChange = onClick)

        Spacer(modifier = Modifier.width(8.dp))

        OnDotText(
            text = DEPARTURE_FROM_HOME,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray200,
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun PlaceList(
    list: List<AddressInfo>,
    historyList: List<PlaceHistory>,
    addressInput: String,
    onPlaceSelected: (AddressInfo) -> Unit,
    onHistoryPlaceSelected: (PlaceHistory) -> Unit,
    onHistoryClose: (Int) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp),
    ) {
        if (addressInput.isBlank()) {
            itemsIndexed(historyList, key = { _, item -> "${item.title}_${item.searchedAt}" }) { index, item ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onHistoryPlaceSelected(item) },
                ) {
                    PlaceHistoryItem(
                        item = item,
                        onClick = { onHistoryClose(index) },
                        formatDate = { PlacePickerUiModel.formattedDate(it) },
                    )
                    HorizontalDivider(thickness = (0.5).dp, color = Gray800)
                }
            }
        } else {
            itemsIndexed(list, key = { _, item -> "${item.latitude}_${item.longitude}_${item.title}" }) { index, item ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPlaceSelected(item) },
                ) {
                    PlaceSearchResultItem(addressInput = addressInput, item = item)
                    HorizontalDivider(thickness = (0.5).dp, color = Gray800)
                }
            }
        }
    }
}
