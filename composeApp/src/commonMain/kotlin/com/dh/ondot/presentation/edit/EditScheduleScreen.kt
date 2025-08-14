package com.dh.ondot.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.core.util.DateTimeFormatter.toLocalDateFromIso
import com.dh.ondot.core.util.DateTimeFormatter.toLocalTimeFromIso
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.TimeType
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.edit.bottomSheet.EditDateBottomSheet
import com.dh.ondot.presentation.edit.bottomSheet.EditTimeBottomSheet
import com.dh.ondot.presentation.general.check.components.AlarmInfoItem
import com.dh.ondot.presentation.general.place.components.RouteInputSection
import com.dh.ondot.presentation.ui.components.DateTimeInfoBar
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotDialog
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DELETE_ALARM_CONTENT
import com.dh.ondot.presentation.ui.theme.DELETE_ALARM_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientGreenBottom
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientGreenTop
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.OnDotTypo
import com.dh.ondot.presentation.ui.theme.WORD_DELETE
import com.dh.ondot.presentation.ui.theme.WORD_SAVE
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_pencil_white
import org.jetbrains.compose.resources.painterResource

@Composable
fun EditScheduleScreen(
    scheduleId: Long,
    viewModel: EditScheduleViewModel = viewModel { EditScheduleViewModel() },
    popScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        delay(200)
        if (uiState.scheduleId == -1L) viewModel.updateScheduleId(scheduleId)
        if (!uiState.isInitialized) { viewModel.getScheduleDetail(scheduleId) }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is EditScheduleEvent.NavigateToHomeScreen -> popScreen()
            }
        }
    }

    if (uiState.isInitialized) {
        EditScheduleContent(
            uiState = uiState,
            interactionSource = interactionSource,
            focusRequester = focusRequester,
            onClickClose = popScreen,
            onValueChanged = viewModel::updateScheduleTitle,
            onToggleSwitch = viewModel::toggleSwitch,
            onSaveSchedule = viewModel::saveSchedule,
            onShowDeleteDialog = viewModel::showDeleteDialog,
            onDeleteSchedule = viewModel::deleteSchedule,
            onDismissDialog = viewModel::hideDeleteDialog,
            onEditDate = viewModel::editDate,
            onEditTime = viewModel::editTime,
            onShowDateBottomSheet = viewModel::showDateBottomSheet,
            onShowTimeBottomSheet = viewModel::showTimeBottomSheet,
            onDismissDateBottomSheet = viewModel::hideDateBottomSheet,
            onDismissTimeBottomSheet = viewModel::hideTimeBottomSheet
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Gray900))
    }
}

@Composable
fun EditScheduleContent(
    uiState: EditScheduleUiState,
    interactionSource: MutableInteractionSource,
    focusRequester: FocusRequester,
    onClickClose: () -> Unit,
    onValueChanged: (String) -> Unit,
    onToggleSwitch: () -> Unit,
    onSaveSchedule: () -> Unit,
    onShowDeleteDialog: () -> Unit,
    onDeleteSchedule: () -> Unit,
    onDismissDialog: () -> Unit,
    onEditDate: (Boolean, Set<Int>, LocalDate?) -> Unit,
    onEditTime: (LocalDate, LocalTime) -> Unit,
    onShowDateBottomSheet: () -> Unit,
    onShowTimeBottomSheet: (TimeType) -> Unit,
    onDismissDateBottomSheet: () -> Unit,
    onDismissTimeBottomSheet: () -> Unit
) {
    val appointmentDate = uiState.schedule.appointmentAt.toLocalTimeFromIso()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GradientGreenTop)
                        .padding(horizontal = 22.dp)
                ) {
                    TopBarSection(
                        scheduleTitle = uiState.schedule.title,
                        focusRequester = focusRequester,
                        onClickClose = onClickClose,
                        onValueChanged = onValueChanged
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GradientGreenBottom)
                            .padding(horizontal = 22.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        DateTimeInfoBar(
                            repeatDays = uiState.schedule.repeatDays,
                            date = uiState.selectedDate,
                            time = appointmentDate,
                            interactionSource = interactionSource,
                            onClickDate = onShowDateBottomSheet,
                            onClickTime = { onShowTimeBottomSheet(TimeType.APPOINTMENT) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        RouteInputSection(
                            departurePlaceInput = uiState.schedule.departurePlace.title,
                            arrivalPlaceInput = uiState.schedule.arrivalPlace.title,
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
                            info = uiState.schedule.preparationAlarm,
                            type = AlarmType.Preparation,
                            scheduleDate = uiState.schedule.appointmentAt,
                            interactionSource = interactionSource,
                            onClick = { onShowTimeBottomSheet(TimeType.PREPARATION) },
                            onToggleSwitch = onToggleSwitch
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AlarmInfoItem(
                            info = uiState.schedule.departureAlarm,
                            type = AlarmType.Departure,
                            scheduleDate = uiState.schedule.appointmentAt,
                            interactionSource = interactionSource,
                            onClick = { onShowTimeBottomSheet(TimeType.DEPARTURE) }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        DeleteScheduleButton(onClick = onShowDeleteDialog)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OnDotButton(
                buttonText = WORD_SAVE,
                buttonType = ButtonType.Green500,
                modifier = Modifier.padding(horizontal = 22.dp),
                onClick = onSaveSchedule
            )
        }

        if (uiState.showDeleteDialog) {
            OnDotDialog(
                dialogTitle = DELETE_ALARM_TITLE,
                dialogContent = DELETE_ALARM_CONTENT,
                onPositiveClick = onDeleteSchedule,
                onNegativeClick = onDismissDialog,
                onDismiss = onDismissDialog
            )
        }

        if (uiState.showDateBottomSheet) {
            AnimatedVisibility(
                visible = uiState.showDateBottomSheet,
                modifier = Modifier.fillMaxSize(),
                enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                EditDateBottomSheet(
                    isRepeat = uiState.schedule.isRepeat,
                    repeatDays = uiState.schedule.repeatDays.toSet(),
                    currentDate = uiState.schedule.appointmentAt.toLocalDateFromIso(),
                    onEditDate = onEditDate,
                    onDismiss = onDismissDateBottomSheet
                )
            }
        }

        if (uiState.showScheduleTimeBottomSheet) {
            AnimatedVisibility(
                visible = uiState.showScheduleTimeBottomSheet,
                modifier = Modifier.fillMaxSize(),
                enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                EditTimeBottomSheet(
                    currentTime = uiState.selectedTime,
                    onDismiss = {
                        onDismissTimeBottomSheet()
                    },
                    onTimeSelected = { date, time ->
                        onEditTime(date, time)
                        onDismissTimeBottomSheet()
                    }
                )
            }
        }

        if (uiState.showAlarmTimeBottomSheet) {
            AnimatedVisibility(
                visible = uiState.showAlarmTimeBottomSheet,
                modifier = Modifier.fillMaxSize(),
                enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                EditTimeBottomSheet(
                    currentTime = uiState.selectedTime,
                    currentAlarmDate = uiState.selectedAlarmDate,
                    isAlarm = true,
                    scheduleDate = uiState.selectedDate,
                    onDismiss = {
                        onDismissTimeBottomSheet()
                    },
                    onTimeSelected = { date, time ->
                        onEditTime(date, time)
                        onDismissTimeBottomSheet()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteScheduleButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        OnDotText(
            text = WORD_DELETE,
            style = OnDotTextStyle.BodyLargeSB,
            color = Red
        )
    }
}

@Composable
private fun TopBarSection(
    scheduleTitle: String,
    focusRequester: FocusRequester,
    onClickClose: () -> Unit,
    onValueChanged: (String) -> Unit
) {
    var input by remember { mutableStateOf(scheduleTitle) }

    TopBar(
        type = TopBarType.CLOSE,
        buttonColor = Gray800,
        onClick = onClickClose,
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