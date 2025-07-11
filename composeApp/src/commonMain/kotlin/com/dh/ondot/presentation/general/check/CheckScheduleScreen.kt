package com.dh.ondot.presentation.general.check

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.general.GeneralScheduleEvent
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.check.components.AlarmInfoItem
import com.dh.ondot.presentation.general.place.components.RouteInputSection
import com.dh.ondot.presentation.ui.components.DateTimeInfoBar
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientGreenTop
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotTypo
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_pencil_white
import org.jetbrains.compose.resources.painterResource

@Composable
fun CheckScheduleScreen(
    viewModel: GeneralScheduleViewModel,
    popScreen: () -> Unit,
    navigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequest = remember { FocusRequester() }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when (it) {
                is GeneralScheduleEvent.NavigateToMain -> navigateToMain()
            }
        }
    }

    CheckScheduleContent(
        uiState = uiState,
        focusRequester = focusRequest,
        onClickBack = popScreen,
        onCreateSchedule = viewModel::createSchedule,
        onValueChanged = viewModel::updateScheduleTitle,
        onToggleSwitch = viewModel::updatePreparationAlarmEnabled
    )
}

@Composable
fun CheckScheduleContent(
    uiState: GeneralScheduleUiState,
    focusRequester: FocusRequester,
    onClickBack: () -> Unit,
    onCreateSchedule: () -> Unit,
    onValueChanged: (String) -> Unit,
    onToggleSwitch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GradientGreenTop)
                .padding(horizontal = 22.dp)
        ) {
            TopBarSection(
                scheduleTitle = uiState.scheduleTitle,
                focusRequester = focusRequester,
                onClickBack = onClickBack,
                onValueChanged = onValueChanged
            )

            if (uiState.selectedDate != null && uiState.selectedTime != null) {
                Spacer(modifier = Modifier.height(24.dp))
                DateTimeInfoBar(date = uiState.selectedDate, time = uiState.selectedTime)
            }

            Spacer(modifier = Modifier.height(16.dp))

            RouteInputSection(
                departurePlaceInput = uiState.departurePlaceInput,
                arrivalPlaceInput = uiState.arrivalPlaceInput,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {
            AlarmInfoItem(
                info = uiState.preparationAlarm,
                type = AlarmType.Preparation,
                onToggleSwitch = onToggleSwitch
            )

            Spacer(modifier = Modifier.height(20.dp))

            AlarmInfoItem(
                info = uiState.departureAlarm,
                type = AlarmType.Departure
            )

            Spacer(modifier = Modifier.weight(1f))

            OnDotButton(
                buttonText = CREATE_SCHEDULE,
                buttonType = ButtonType.Gradient,
                onClick = onCreateSchedule
            )
        }
    }
}

@Composable
private fun TopBarSection(
    scheduleTitle: String,
    focusRequester: FocusRequester,
    onClickBack: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    var input by remember { mutableStateOf(scheduleTitle) }

    TopBar(
        type = TopBarType.BACK,
        buttonColor = Gray800,
        onClick = onClickBack,
        content = {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    singleLine = true,
                    textStyle = OnDotTypo().titleSmallM.copy(color = Gray800),
                    cursorBrush = SolidColor(Gray0),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onValueChanged(input)
                            focusRequester.freeFocus()
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (!it.isFocused) onValueChanged(input)
                        }
                )

                Image(
                    painter = painterResource(Res.drawable.ic_pencil_white),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { focusRequester.requestFocus() },
                    colorFilter = ColorFilter.tint(Gray800)
                )
            }
        }
    )
}