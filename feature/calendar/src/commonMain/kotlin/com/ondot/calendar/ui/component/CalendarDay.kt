package com.ondot.calendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ondot.calendar.contract.CalendarDayCell
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray100
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Green400
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.designsystem.theme.OnDotColor.Green700
import com.ondot.designsystem.theme.OnDotColor.Green900
import com.ondot.designsystem.theme.OnDotColor.Red
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.ui.util.noRippleClickable
import kotlinx.datetime.DayOfWeek

@Composable
fun CalendarDay(
    cell: CalendarDayCell,
    eventLabelAlpha: Float,
    eventDotAlpha: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isInCurrentMonth = cell.inCurrentMonth
    val dateColor =
        when (cell.date.dayOfWeek) {
            DayOfWeek.SUNDAY ->
                if (isInCurrentMonth) Red else Gray400
            DayOfWeek.SATURDAY ->
                if (isInCurrentMonth) Green700 else Gray400
            else ->
                if (isInCurrentMonth) Gray100 else Gray400
        }

    Column(
        modifier =
            modifier
                .noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (cell.isSelected) Green900 else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            OnDotText(
                text = cell.date.day.toString(),
                style = OnDotTextStyle.BodyLargeR2,
                color = if (cell.isSelected) Green400 else dateColor,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            val chipScale = 0.82f + (0.18f * eventLabelAlpha)
            val dotScale = 0.7f + (0.3f * eventDotAlpha)
            val chipTranslateY = -(6f * eventDotAlpha)
            val dotTranslateY = 6f * (1f - eventDotAlpha)

            if (cell.markers.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .graphicsLayer {
                                alpha = eventLabelAlpha
                                scaleX = chipScale
                                scaleY = chipScale
                                translationY = chipTranslateY
                            },
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        cell.markers.take(2).forEach { marker ->
                            ScheduleChip(
                                text = marker.title,
                            )
                        }
                    }
                }

                Box(
                    modifier =
                        Modifier
                            .graphicsLayer {
                                alpha = eventDotAlpha
                                scaleX = dotScale
                                scaleY = dotScale
                                translationY = dotTranslateY
                            },
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        cell.markers.take(2).forEach { _ ->
                            Box(
                                modifier =
                                    Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Green500),
                            )
                        }
                    }
                }
            }
        }
    }
}
