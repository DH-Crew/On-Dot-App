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
import com.ondot.designsystem.components.SwipeableDeleteItem
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray400
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.ui.util.noRippleClickable
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
    togglingScheduleIds: Set<Long> = emptySet(),
    onToggleAlarm: (Long, Boolean) -> Unit = { _, _ -> },
    onDelete: (Long, Boolean) -> Unit = { _, _ -> }, // 반복 일정이 아닌 경우 그냥 삭제
    onClickSchedule: (Long) -> Unit = {},
    onShowScheduleDeleteDialog: (Long) -> Unit = {}, // 반복 일정인 경우 삭제 다이얼로그 띄우기
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
                        SwipeableDeleteItem(
                            onDelete = {
                                if (!item.isPast && item.isRepeat) {
                                    onShowScheduleDeleteDialog(item.scheduleId)
                                } else {
                                    onDelete(item.scheduleId, item.isPast)
                                }
                            },
                        ) {
                            CalendarScheduleListItem(
                                item = item,
                                isToggling = item.scheduleId in togglingScheduleIds,
                                onToggleAlarm = { enabled ->
                                    onToggleAlarm(item.scheduleId, enabled)
                                },
                                onClick = { if (!item.isPast) onClickSchedule(item.scheduleId) },
                            )
                        }
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
    isToggling: Boolean,
    onToggleAlarm: (Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    val leadingBarColor = if (item.isPast) Gray400 else Green500

    Column(
        modifier =
            Modifier
                .background(Gray700)
                .padding(vertical = 8.dp)
                .noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.Start,
    ) {
        if (!item.isPast && item.isRepeat) {
            val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_repeat),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(16.dp),
                )

                Spacer(Modifier.width(2.dp))

                dayLabels.forEachIndexed { index, label ->
                    val isActive = item.repeatDays.contains(index + 1)
                    OnDotText(
                        text = label,
                        style = OnDotTextStyle.BodySmallR1,
                        color = if (isActive) Green500 else Gray400,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = (1.5).dp),
                    )
                }
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
                        if (!item.isPast && !isToggling) {
                            onToggleAlarm(it)
                        }
                    },
                )
            }
        }
    }
}
