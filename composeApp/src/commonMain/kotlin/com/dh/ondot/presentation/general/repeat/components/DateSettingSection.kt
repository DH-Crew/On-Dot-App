package com.dh.ondot.presentation.general.repeat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.formatKorean
import com.dh.ondot.domain.model.constans.AppConstants
import com.dh.ondot.domain.model.enums.ChipStyle
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TextChip
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray100
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.WORD_DATE
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_arrow_left_small
import ondot.composeapp.generated.resources.ic_arrow_right_small
import org.jetbrains.compose.resources.painterResource

@Composable
fun DateSettingSection(
    uiState: GeneralScheduleUiState,
    onToggleCalendar: () -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp)
    ) {
        DateSectionHeader(
            selectedDate = uiState.selectedDate,
            isActiveCalendar = uiState.isActiveCalendar,
            isRepeat = uiState.isRepeat,
            activeWeekDays = uiState.activeWeekDays,
            onToggleCalendar = onToggleCalendar
        )

//        Spacer(modifier = Modifier.height(16.dp))

//        HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

        if (uiState.isActiveCalendar) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

            Spacer(modifier = Modifier.height(8.dp))

            Calendar(
                month = uiState.calendarMonth,
                selectedDate = uiState.selectedDate,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
fun DateSectionHeader(
    selectedDate: LocalDate?,
    isActiveCalendar: Boolean,
    isRepeat: Boolean,
    activeWeekDays: Set<Int>,
    onToggleCalendar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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

@Composable
fun Calendar(
    month: LocalDate,
    selectedDate: LocalDate?,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        DateNavigation(month = month, onPrevMonth = onPrevMonth, onNextMonth = onNextMonth)

        Spacer(modifier = Modifier.height(14.dp))

        CalendarHeader()

        Spacer(modifier = Modifier.height(4.dp))

        CalendarContent(month = month, selectedDate = selectedDate, onDateSelected = onDateSelected)
    }
}

@Composable
fun DateNavigation(
    month: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))

        OnDotText(
            text = "${month.year}년 ${month.monthNumber}월",
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(Res.drawable.ic_arrow_left_small),
            contentDescription = null,
            modifier = Modifier.size(39.dp).clickable { onPrevMonth() }
        )

        Image(
            painter = painterResource(Res.drawable.ic_arrow_right_small),
            contentDescription = null,
            modifier = Modifier.size(39.dp).clickable { onNextMonth() }
        )
    }
}

@Composable
fun CalendarHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppConstants.weekDays.forEachIndexed { index, label ->
            val color = if (index == 0) Red else Gray100

            Box(
                modifier = Modifier
                    .size(39.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                OnDotText(
                    text = label,
                    style = OnDotTextStyle.BodyLargeR2,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CalendarContent(
    month: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val offset = month.dayOfWeek.isoDayNumber % 7   // 일요일(7) -> 0으로 시작
    val days = DateTimeFormatter.monthDays(month.year, month.monthNumber)
    val totalCells = ((offset + days.size + 6) / 7) * 7

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (week in 0 until totalCells / 7) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayIndex in 0 until 7) {
                    val cellIndex = week * 7 + dayIndex
                    val date = if (cellIndex in offset until offset + days.size) {
                        days[cellIndex - offset]
                    } else null

                    DayCell(
                        date = date,
                        isSelected = date != null && date == selectedDate,
                        onClick = { date?.let(onDateSelected) }
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(39.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Green900
                else Color.Transparent
            )
            .clickable(enabled = date != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        OnDotText(
            text = date?.dayOfMonth?.toString().orEmpty(),
            style = OnDotTextStyle.BodyLargeR2,
            color = if (isSelected) Green400 else Gray100
        )
    }
}