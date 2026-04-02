package com.ondot.calendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.CALENDAR_EMPTY_SCHEDULES_GUIDE
import com.ondot.calendar.contract.CalendarScheduleItemUiModel
import com.ondot.designsystem.components.OnDotSwitch
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.util.DateTimeFormatter.formatKoreanMonthDay
import kotlinx.datetime.LocalDate
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_no_clock
import ondot.core.design_system.generated.resources.ic_repeat
import org.jetbrains.compose.resources.painterResource

@Composable
fun CalendarBottomSheet(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    schedules: List<CalendarScheduleItemUiModel>,
    onToggleAlarm: (Long, Boolean) -> Unit = { _, _ -> },
) {
    Surface(
        modifier = modifier,
        color = Gray700,
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(vertical = 12.dp)
                        .align(Alignment.CenterHorizontally)
                        .width(32.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Gray300),
            )

            OnDotText(
                text = selectedDate.formatKoreanMonthDay(),
                color = Gray0,
                style = OnDotTextStyle.TitleSmallSB,
                modifier =
                    Modifier
                        .fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            if (schedules.isEmpty()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_no_clock),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(28.dp),
                        colorFilter = ColorFilter.tint(Gray200),
                    )

                    Spacer(Modifier.width(8.dp))

                    OnDotText(
                        text = CALENDAR_EMPTY_SCHEDULES_GUIDE,
                        style = OnDotTextStyle.BodyLargeR1,
                        color = Gray200,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Spacer(Modifier.height(20.dp))
                    schedules.forEach { item ->
                        CalendarScheduleListItem(
                            item = item,
                            onToggleAlarm = { enabled ->
                                onToggleAlarm(item.scheduleId, enabled)
                            },
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarScheduleListItem(
    item: CalendarScheduleItemUiModel,
    onToggleAlarm: (Boolean) -> Unit,
) {
    val leadingBarColor = if (item.isPast) Gray400 else Green500

    Column(
        modifier =
            Modifier
                .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        if (item.repeatText != null && !item.isPast) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_repeat),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(16.dp),
                )
                OnDotText(
                    text = item.repeatText,
                    style = OnDotTextStyle.BodySmallR2,
                    color = Green500,
                )
            }
            Spacer(Modifier.height(4.dp))
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .padding(vertical = 3.dp)
                            .width(4.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(99.dp))
                            .background(leadingBarColor),
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    OnDotText(
                        text = "${item.title} ${item.appointmentTimeText}",
                        style = OnDotTextStyle.BodyLargeR1,
                        color = Gray0,
                    )

                    Spacer(Modifier.height(4.dp))

                    OnDotText(
                        text = item.alarmInfoText,
                        style = OnDotTextStyle.BodyMediumR,
                        color = Gray300,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            if (!item.isPast) {
                OnDotSwitch(
                    modifier =
                        Modifier
                            .padding(vertical = 4.dp),
                    checked = item.isAlarmEnabled,
                    onClick = {
                        if (!item.isPast) onToggleAlarm(it)
                    },
                )
            }
        }
    }
}
