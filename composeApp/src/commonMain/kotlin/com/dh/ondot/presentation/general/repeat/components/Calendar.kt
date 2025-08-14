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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.constants.AppConstants
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray100
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_arrow_left_small
import ondot.composeapp.generated.resources.ic_arrow_right_small
import org.jetbrains.compose.resources.painterResource

@Composable
fun Calendar(
    month: LocalDate,
    selectedDate: LocalDate?,
    isRepeat: Boolean,

    // 알람 시간 수정
    isAlarm: Boolean = false,
    scheduleDate: LocalDate? = null,

    activeWeekDays: Set<Int>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val possibleDate = scheduleDate?.minus(DatePeriod(days = 1))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        DateNavigation(month = month, onPrevMonth = onPrevMonth, onNextMonth = onNextMonth)

        Spacer(modifier = Modifier.height(14.dp))

        CalendarHeader()

        Spacer(modifier = Modifier.height(4.dp))

        CalendarContent(
            month = month,
            selectedDate = selectedDate,
            isRepeat = isRepeat,
            isAlarm = isAlarm,
            scheduleDate = scheduleDate,
            possibleDate = possibleDate,
            activeWeekDays = activeWeekDays,
            onDateSelected = onDateSelected
        )
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
    isRepeat: Boolean,

    // 알람 시간 수정
    isAlarm: Boolean,
    scheduleDate: LocalDate?,
    possibleDate: LocalDate?,

    activeWeekDays: Set<Int>,
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
                    val isSelected = when {
                        date == null -> false
                        isRepeat -> activeWeekDays.contains(date.dayOfWeek.isoDayNumber % 7)
                        else -> date == selectedDate
                    }
                    val isPossible = date == possibleDate || date == scheduleDate

                    DayCell(
                        date = date,
                        isSelected = isSelected,
                        isPossible = if (isAlarm) isPossible else true,
                        onClick = { if (!isRepeat && date != null) onDateSelected(date) }
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
    isPossible: Boolean,
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
            .clickable(enabled = date != null && isPossible) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        OnDotText(
            text = date?.dayOfMonth?.toString().orEmpty(),
            style = OnDotTextStyle.BodyLargeR2,
            color =
                if (isPossible) { if (isSelected) Green400 else Gray100 }
                else Gray500
        )
    }
}