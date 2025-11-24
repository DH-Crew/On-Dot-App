package com.dh.ondot.presentation.general.check

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.general.GeneralScheduleEvent
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.general.check.components.AlarmInfoItem
import com.dh.ondot.presentation.general.place.components.RouteInputSection
import com.dh.ondot.presentation.ui.components.DateTimeInfoBar
import com.dh.ondot.presentation.ui.components.OnDotBottomSheet
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotCheckBox
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.RoundedTextField
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.CREATE_SCHEDULE
import com.dh.ondot.presentation.ui.theme.GENERAL_SCHEDULE_BOTTOM_SHEET_MATERIAL
import com.dh.ondot.presentation.ui.theme.GENERAL_SCHEDULE_BOTTOM_SHEET_MEDICINE
import com.dh.ondot.presentation.ui.theme.GENERAL_SCHEDULE_BOTTOM_SHEET_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientGreenTop
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotTypo
import com.dh.ondot.presentation.ui.theme.WORD_CONFIRM
import com.dh.ondot.presentation.ui.theme.WORD_FINE
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.TopBarType
import com.ondot.util.AnalyticsLogger
import com.ondot.util.DateTimeFormatter.toIsoDateString
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

    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("screen_view_schedule_review")
    }

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
        onToggleSwitch = viewModel::updatePreparationAlarmEnabled,
        onShowBottomSheet = { viewModel.updateBottomSheetVisible(true) },
        onDismiss = { viewModel.updateBottomSheetVisible(false) }
    )
}

@Composable
fun CheckScheduleContent(
    uiState: GeneralScheduleUiState,
    focusRequester: FocusRequester,
    onClickBack: () -> Unit,
    onCreateSchedule: (Boolean, String) -> Unit,
    onValueChanged: (String) -> Unit,
    onToggleSwitch: () -> Unit,
    onShowBottomSheet: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                    scheduleDate = uiState.selectedDate?.toIsoDateString() ?: "",
                    onToggleSwitch = onToggleSwitch
                )

                Spacer(modifier = Modifier.height(20.dp))

                AlarmInfoItem(
                    info = uiState.departureAlarm,
                    type = AlarmType.Departure,
                    scheduleDate = uiState.selectedDate?.toIsoDateString() ?: ""
                )

                Spacer(modifier = Modifier.weight(1f))

                OnDotButton(
                    buttonText = CREATE_SCHEDULE,
                    buttonType = ButtonType.Gradient,
                    onClick = onShowBottomSheet
                )
            }
        }

        if (uiState.showBottomSheet) {
            AnimatedVisibility(
                visible = uiState.showBottomSheet,
                modifier = Modifier.fillMaxSize(),
                enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                OnDotBottomSheet(
                    onDismiss = onDismiss,
                    content = { BottomSheetContent(onCreateSchedule = onCreateSchedule) }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetContent(
    onCreateSchedule: (Boolean, String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    var isMedicineChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        OnDotText(
            text = GENERAL_SCHEDULE_BOTTOM_SHEET_TITLE,
            style = OnDotTextStyle.TitleSmallSB,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnDotCheckBox(
                isChecked = isMedicineChecked,
                onCheckedChange = { isMedicineChecked = !isMedicineChecked }
            )

            Spacer(modifier = Modifier.width(8.dp))

            OnDotText(
                text = GENERAL_SCHEDULE_BOTTOM_SHEET_MEDICINE,
                style = OnDotTextStyle.BodyLargeR1,
                color = Gray200,
                modifier = Modifier.clickable { isMedicineChecked = !isMedicineChecked }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RoundedTextField(
            value = input,
            onValueChange = { input = it },
            placeholder = GENERAL_SCHEDULE_BOTTOM_SHEET_MATERIAL,
            maxLength = 100,
            maxLines = 10,
            singleLine = false,
            backgroundColor = Gray600,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(26.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OnDotButton(
                buttonText = WORD_FINE,
                buttonType = ButtonType.Gray400,
                buttonHeight = 48.dp,
                modifier = Modifier.weight(1f),
                onClick = { onCreateSchedule(false, "") }
            )

            Spacer(modifier = Modifier.width(11.dp))

            OnDotButton(
                buttonText = WORD_CONFIRM,
                buttonType = ButtonType.Green500,
                buttonHeight = 48.dp,
                modifier = Modifier.weight(1f),
                onClick = { onCreateSchedule(isMedicineChecked, input) }
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
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
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