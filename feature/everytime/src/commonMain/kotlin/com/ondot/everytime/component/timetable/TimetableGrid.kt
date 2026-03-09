package com.ondot.everytime.component.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientTimetable
import com.dh.ondot.presentation.ui.theme.OnDotColor.GradientTimetableUnselected
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray800
import com.ondot.designsystem.components.OnDotText
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.everytime.contract.TimetableClassUiModel
import com.ondot.ui.util.noRippleClickable
import kotlinx.datetime.LocalTime

@Composable
fun TimetableGrid(
    classes: List<TimetableClassUiModel>,
    modifier: Modifier = Modifier,
    onClickClass: (TimetableClassUiModel) -> Unit,
) {
    val scrollState = rememberScrollState()
    val baseDays =
        listOf(
            DayOfWeekKey.MONDAY,
            DayOfWeekKey.TUESDAY,
            DayOfWeekKey.WEDNESDAY,
            DayOfWeekKey.THURSDAY,
            DayOfWeekKey.FRIDAY,
        )
    // 토요일, 일요일에도 시간표가 있으면 예외적으로 추가
    val extraDays =
        listOf(DayOfWeekKey.SATURDAY, DayOfWeekKey.SUNDAY)
            .filter { extraDay -> classes.any { it.day == extraDay } }
    val days = baseDays + extraDays

    val rowHeight = 36.dp
    val columnSpacing = 4.dp
    val rowSpacing = 4.dp

    val startHour = 9
    val endHour = 21
    val rowCount = endHour - startHour

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(columnSpacing),
        ) {
            days.forEach { day ->
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    OnDotText(
                        text = day.toKorLabel(),
                        style = OnDotTextStyle.BodyLargeR2,
                        color = Gray200,
                    )
                }
            }
        }

        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
        ) {
            val totalSpacing = columnSpacing * (days.size - 1)
            val columnWidth = (maxWidth - totalSpacing) / days.size
            val gridHeight = rowHeight * rowCount + rowSpacing * (rowCount - 1)

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(columnSpacing),
                ) {
                    repeat(days.size) {
                        Column(
                            modifier = Modifier.width(columnWidth),
                            verticalArrangement = Arrangement.spacedBy(rowSpacing),
                        ) {
                            repeat(rowCount) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(rowHeight)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Gray800),
                                )
                            }
                        }
                    }
                }

                classes.forEach { item ->
                    val dayIndex = days.indexOf(item.day)
                    if (dayIndex == -1) return@forEach

                    val startMinute = item.startTime.toTotalMinutes()
                    val endMinute = item.endTime.toTotalMinutes()
                    val baseMinute = startHour * 60

                    val startFromBase = (startMinute - baseMinute).coerceAtLeast(0)
                    val endFromBase = (endMinute - baseMinute).coerceAtLeast(startFromBase + 15)

                    val topOffset = minuteToOffset(startFromBase, rowHeight, rowSpacing)
                    val bottomOffset = minuteToOffset(endFromBase, rowHeight, rowSpacing)
                    val blockHeight = (bottomOffset - topOffset).coerceAtLeast(36.dp)

                    val xOffset = (columnWidth + columnSpacing) * dayIndex

                    TimetableClassItem(
                        item = item,
                        modifier =
                            Modifier
                                .width(columnWidth)
                                .height(blockHeight)
                                .offset(x = xOffset, y = topOffset),
                        onClick = { onClickClass(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TimetableClassItem(
    item: TimetableClassUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val backgroundColor = if (item.isSelected) GradientTimetable else GradientTimetableUnselected
    val textColor = Gray0

    Box(
        modifier =
            modifier
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(6.dp)
                .noRippleClickable { onClick() },
        contentAlignment = Alignment.TopCenter,
    ) {
        OnDotText(
            text = item.courseName,
            style = OnDotTextStyle.BodySmallR3,
            color = textColor,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun minuteToOffset(
    minuteFromBase: Int,
    rowHeight: Dp,
    rowSpacing: Dp,
): Dp {
    val hourBlock = minuteFromBase / 60
    val minuteInHour = minuteFromBase % 60
    val minuteHeight = rowHeight / 60f

    return (hourBlock * (rowHeight + rowSpacing)) + (minuteInHour * minuteHeight)
}

private fun DayOfWeekKey.toKorLabel(): String =
    when (this) {
        DayOfWeekKey.MONDAY -> "월"
        DayOfWeekKey.TUESDAY -> "화"
        DayOfWeekKey.WEDNESDAY -> "수"
        DayOfWeekKey.THURSDAY -> "목"
        DayOfWeekKey.FRIDAY -> "금"
        DayOfWeekKey.SATURDAY -> "토"
        DayOfWeekKey.SUNDAY -> "일"
    }

private fun LocalTime.toTotalMinutes(): Int = hour * 60 + minute
