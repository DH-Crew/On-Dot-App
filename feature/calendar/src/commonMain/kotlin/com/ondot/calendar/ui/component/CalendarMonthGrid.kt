package com.ondot.calendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ondot.calendar.contract.CalendarDayCell
import com.ondot.designsystem.theme.OnDotColor.Gray700
import kotlinx.datetime.LocalDate
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun CalendarMonthGrid(
    cells: List<CalendarDayCell>,
    eventLabelAlpha: Float,
    eventDotAlpha: Float,
    bodyHeight: Dp,
    onSelectDate: (LocalDate) -> Unit,
) {
    val weeks = cells.chunked(7)
    val weekCount = weeks.size
    val verticalGap = 4.dp
    val dividerThickness = 1.dp
    val dividerCount = weekCount - 1
    val childCount = weekCount + dividerCount
    val spacingCount = childCount - 1
    val totalSpacing = verticalGap * spacingCount
    val totalDividerHeight = dividerThickness * dividerCount
    val rowHeight =
        ((bodyHeight - totalSpacing - totalDividerHeight) / weekCount)
            .coerceAtLeast(0.dp)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(verticalGap),
    ) {
        weeks.forEachIndexed { index, week ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(rowHeight),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                week.forEach { cell ->
                    CalendarDay(
                        cell = cell,
                        eventLabelAlpha = eventLabelAlpha,
                        eventDotAlpha = eventDotAlpha,
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        onClick = { onSelectDate(cell.date) },
                    )
                }
            }

            if (index != weeks.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = Gray700)
            }
        }
    }
}
