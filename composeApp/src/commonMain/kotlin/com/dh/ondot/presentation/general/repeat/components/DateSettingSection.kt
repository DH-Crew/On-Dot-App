package com.dh.ondot.presentation.general.repeat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TextChip
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.WORD_DATE
import com.dh.ondot.presentation.ui.theme.WORD_TIME
import com.ondot.domain.model.constants.AppConstants
import com.ondot.domain.model.enums.ChipStyle
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.util.DateTimeFormatter.formatAmPmTime
import com.ondot.util.DateTimeFormatter.formatKorean
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun DateSettingSection(
    uiState: GeneralScheduleUiState,
    interactionSource: MutableInteractionSource,
    onToggleCalendar: () -> Unit,
    onToggleDial: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp)
    ) {
        DateSectionHeader(
            selectedDate = uiState.selectedDate,
            isActiveCalendar = uiState.isActiveCalendar,
            interactionSource = interactionSource,
            isRepeat = uiState.isRepeat,
            activeWeekDays = uiState.activeWeekDays,
            onToggleCalendar = onToggleCalendar
        )

        if (uiState.isActiveCalendar && !uiState.isRepeat) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

            Spacer(modifier = Modifier.height(8.dp))

            Calendar(
                month = uiState.calendarMonth,
                selectedDate = uiState.selectedDate,
                isRepeat = uiState.isRepeat,
                activeWeekDays = uiState.activeWeekDays,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
                onDateSelected = onDateSelected
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

        Spacer(modifier = Modifier.height(16.dp))

        TimeSectionHeader(
            selectedTime = uiState.selectedTime,
            isActiveDial = uiState.isActiveDial,
            interactionSource = interactionSource,
            onToggleDial = onToggleDial
        )

        if (uiState.isActiveDial) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

            TimePicker(
                periodState = periodState,
                hourState = hourState,
                minuteState = minuteState,
                onTimeSelected = onTimeSelected
            )
        }
    }
}

@Composable
fun TimeSectionHeader(
    selectedTime: LocalTime?,
    isActiveDial: Boolean,
    interactionSource: MutableInteractionSource,
    onToggleDial: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onToggleDial() }
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = WORD_TIME,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0
        )

        Spacer(modifier = Modifier.weight(1f))

        TextChip(
            text = selectedTime?.formatAmPmTime() ?: "-",
            chipStyle = if (isActiveDial) ChipStyle.Active else ChipStyle.Normal,
            onClick = onToggleDial
        )
    }
}

@Composable
fun DateSectionHeader(
    selectedDate: LocalDate?,
    isActiveCalendar: Boolean,
    interactionSource: MutableInteractionSource,
    isRepeat: Boolean,
    activeWeekDays: Set<Int>,
    onToggleCalendar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onToggleCalendar() }
            )
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = WORD_DATE,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isRepeat && activeWeekDays.isNotEmpty()) {
            AppConstants.weekDays.forEachIndexed { index, label ->
                OnDotText(
                    text = label,
                    style = OnDotTextStyle.BodyMediumR,
                    color = if (activeWeekDays.contains(index)) Green500 else Gray400,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        } else {
            TextChip(
                text = selectedDate?.formatKorean() ?: "-",
                chipStyle = if (isActiveCalendar) ChipStyle.Active else ChipStyle.Normal,
                onClick = onToggleCalendar
            )
        }
    }
}