package com.ondot.edit.bottomSheet

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.WORD_COMPETE
import com.ondot.design_system.components.Calendar
import com.ondot.design_system.components.DateSectionHeader
import com.ondot.design_system.components.OnDotBottomSheet
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.TimePicker
import com.ondot.design_system.components.TimeSectionHeader
import com.ondot.domain.model.enums.ButtonType
import com.ondot.util.AnalyticsLogger
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun EditTimeBottomSheet(
    currentTime: LocalTime,
    currentAlarmDate: LocalDate? = null,
    scheduleDate: LocalDate? = null,
    isAlarm: Boolean = false,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalDate, LocalTime) -> Unit,
) {
    val viewModel: EditBottomSheetViewModel = viewModel { EditBottomSheetViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = currentTime.hour.coerceIn(0, 23))
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = currentTime.minute.coerceIn(0, 59))

    LaunchedEffect(Unit) {
        AnalyticsLogger.logEvent("bottom_sheet_view_edit_time")
    }

    LaunchedEffect(Unit) {
        viewModel.initTime(currentTime)
        currentAlarmDate?.let { viewModel.initDate(isRepeat = false, repeatDays = emptySet(), currentDate = it) }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    if (uiState.currentTime != null) {
        OnDotBottomSheet(
            onDismiss = onDismiss,
            contentPaddingTop = if (isAlarm) 32.dp else 16.dp,
            contentPaddingBottom = 16.dp,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (isAlarm) {
                        DateSectionHeader(
                            selectedDate = uiState.currentDate,
                            isActiveCalendar = uiState.isActiveCalendar,
                            isRepeat = false,
                            activeWeekDays = emptySet(),
                            interactionSource = interactionSource,
                            onToggleCalendar = viewModel::onToggleCalendar
                        )

                        if (uiState.isActiveCalendar) {
                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.fillMaxWidth())

                            Calendar(
                                month = uiState.calendarMonth,
                                selectedDate = uiState.currentDate,
                                isRepeat = uiState.isRepeat,
                                isAlarm = isAlarm,
                                scheduleDate = scheduleDate,
                                activeWeekDays = uiState.repeatDays,
                                onPrevMonth = viewModel::onPrevMonth,
                                onNextMonth = viewModel::onNextMonth,
                                onDateSelected = viewModel::onDateSelected
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TimeSectionHeader(
                        selectedTime = uiState.currentTime,
                        isActiveDial = true,
                        interactionSource = interactionSource,
                        onToggleDial = {}
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

                    TimePicker(
                        periodState = periodState,
                        hourState = hourState,
                        minuteState = minuteState,
                        onTimeSelected = viewModel::onTimeSelected
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    OnDotButton(
                        buttonText = WORD_COMPETE,
                        buttonType = ButtonType.Green500,
                        onClick = {
                            Logger.e { "currentTime: ${uiState.currentTime}" }
                            uiState.currentTime?.let { time ->
                                if (isAlarm) {
                                    uiState.currentDate?.let { date ->
                                        onTimeSelected(date, time)
                                    }
                                } else {
                                    val date = uiState.currentDate ?: scheduleDate ?: Clock.System.now().toLocalDateTime(
                                        TimeZone.currentSystemDefault()).date
                                    onTimeSelected(date, time)
                                }
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        )
    }
}