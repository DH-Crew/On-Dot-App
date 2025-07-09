package com.dh.ondot.presentation.general.repeat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.core.util.DateTimeFormatter.formatAmPmTime
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
import com.dh.ondot.presentation.ui.theme.WORD_AM
import com.dh.ondot.presentation.ui.theme.WORD_DATE
import com.dh.ondot.presentation.ui.theme.WORD_PM
import com.dh.ondot.presentation.ui.theme.WORD_TIME
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_arrow_left_small
import ondot.composeapp.generated.resources.ic_arrow_right_small
import org.jetbrains.compose.resources.painterResource

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
            isRepeat = uiState.isRepeat,
            activeWeekDays = uiState.activeWeekDays,
            onToggleCalendar = onToggleCalendar
        )

        if (uiState.isActiveCalendar) {
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
            chipStyle = if (isActiveDial) ChipStyle.Active else ChipStyle.Normal
        )
    }
}

@Composable
fun DialPicker(
    items: List<String>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    infinite: Boolean = true,
    onValueChange: (String) -> Unit = {}
) {
    val visibleItemCount = 5
    val itemHeight = 33.dp
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 스크롤이 멈춘 시점에 콜백 호출
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .drop(1)
            .filter { inProgress -> !inProgress }
            .map {
                val centerIndex = listState.firstVisibleItemIndex + visibleItemCount / 2

                if (infinite) items[centerIndex % items.size]
                else {
                    val real = centerIndex - 2
                    items.getOrNull(real)?.takeIf { it.isNotEmpty() } ?: ""
                }
            }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .collect { index ->
                onValueChange(index)
            }
    }

    if (infinite) {
        val repeatCount = 1000
        val itemCount = repeatCount * items.size

        // 무한 스크롤에서 가운데 위치로 자동 스크롤
        LaunchedEffect(listState) {
            val middle = itemCount / 2
            val startIndex = middle - (middle % items.size)

            listState.scrollToItem(startIndex - 2)
        }

        LazyColumn(
            state = listState,
            modifier = modifier
                .height(itemHeight * visibleItemCount),
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = itemCount) { index ->
                val text = items[index % items.size]
                val firstVisibleItemIndex = listState.firstVisibleItemIndex
                val color = when (index) {
                    firstVisibleItemIndex + 2 -> Gray0
                    else -> Gray400
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isNotEmpty()) {
                        OnDotText(
                            text = text,
                            style = OnDotTextStyle.TitleSmallR,
                            color = color
                        )
                    }
                }
            }
        }
    } else {
        val extended = listOf("", "") + items + listOf("", "")

        LazyColumn(
            state = listState,
            modifier = modifier.height(itemHeight * visibleItemCount),
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(extended.size) { index ->
                val text = extended[index]
                val firstVisible = listState.firstVisibleItemIndex
                val color = if (index == firstVisible + 2) Gray0 else Gray400

                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isNotEmpty()) {
                        OnDotText(
                            text = text,
                            style = OnDotTextStyle.TitleSmallR,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePicker(
    periodState: LazyListState,
    hourState: LazyListState,
    minuteState: LazyListState,
    modifier: Modifier = Modifier,
    onTimeSelected: (LocalTime) -> Unit
) {
    val period = listOf(WORD_AM, WORD_PM)
    val hour = (1..12).toList().map { it.toString().padStart(2, '0') }
    val minute = (0..55 step 5).toList().map { it.toString().padStart(2, '0') }
    var selPeriod by remember { mutableStateOf(period.first()) }
    var selHour   by remember { mutableStateOf(hour.first()) }
    var selMin    by remember { mutableStateOf(minute.first()) }

    // 값이 하나라도 바뀌면 onTimeSelected 콜백 호출
    LaunchedEffect(selPeriod, selHour, selMin) {
        val h = selHour.toInt().let { hh ->
            when (selPeriod) {
                WORD_PM -> (hh % 12) + 12
                else -> hh % 12
            }
        }
        val m = selMin.toInt()
        onTimeSelected(LocalTime(h, m))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(33.dp)
                .background(Gray600, RoundedCornerShape(8.dp))
        )

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            DialPicker(
                items = period,
                listState = periodState,
                infinite = false,
                onValueChange = { selPeriod = it }
            )

            Spacer(modifier = Modifier.width(46.dp))

            DialPicker(
                items = hour,
                listState = hourState,
                onValueChange = { selHour = it }
            )

            Spacer(modifier = Modifier.width(40.dp))

            DialPicker(
                items = minute,
                listState = minuteState,
                onValueChange = { selMin = it }
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
    isRepeat: Boolean,
    activeWeekDays: Set<Int>,
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

        CalendarContent(
            month = month,
            selectedDate = selectedDate,
            isRepeat        = isRepeat,
            activeWeekDays  = activeWeekDays,
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

                    DayCell(
                        date = date,
                        isSelected = isSelected,
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