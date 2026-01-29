package com.ondot.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.EMPTY_PREPARATION_ALARM
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.WORD_DEPARTURE
import com.dh.ondot.presentation.ui.theme.WORD_PREPARATION
import com.dh.ondot.presentation.ui.theme.appointmentTime
import com.ondot.design_system.components.OnDotSwitch
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.SwipableDeleteItem
import com.ondot.domain.model.enums.AlarmType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.schedule.Schedule
import com.ondot.main.home.HomeUiState
import com.ondot.ui.util.rotatingGlowStroke
import com.ondot.util.DateTimeFormatter
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_notification_banner
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScheduleList(
    earliestScheduleId: Long,
    scheduleList: List<Schedule>,
    interactionSource: MutableInteractionSource,
    onClickSwitch: (Long, Boolean) -> Unit,
    onClickSchedule: (Long) -> Unit,
    onBannerClick: () -> Unit,
    onLongClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Image(
                painter = painterResource(Res.drawable.ic_notification_banner),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(349f/100f)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = onBannerClick
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        itemsIndexed(scheduleList, key = { index, it -> it.scheduleId }) { index, item ->
            SwipableDeleteItem(
                onDelete = { onDelete(item.scheduleId) }
            ) {
                ScheduleListItem(
                    item = item,
                    enabled = earliestScheduleId == item.scheduleId,
                    onClickSwitch = onClickSwitch,
                    onClickSchedule = onClickSchedule,
                    onLongClick = onLongClick
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ScheduleListItem(
    item: Schedule,
    enabled: Boolean = false,
    onClickSwitch: (Long, Boolean) -> Unit,
    onClickSchedule: (Long) -> Unit,
    onLongClick: (Long) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Gray700, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .rotatingGlowStroke(
                enabled = enabled,
                cornerRadius = 12.dp,
                strokeWidth = 1.dp,
                highlightStartColor = Gray700.copy(alpha = 0.4f),
                highlightEndColor = Green500,
                cap = StrokeCap.Butt
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClickSchedule(item.scheduleId) },
                onLongClick = { onLongClick(item.scheduleId) }
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray700)
        ) {
            if (enabled) {
                TopFogOverlay(
                    modifier = Modifier.matchParentSize(),
                    blurRadius = 10.dp,
                    cornerRadius = 12.dp,
                    rightGlowColor = Green500
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                ScheduleInfoToggleSection(
                    date = item.appointmentAt,
                    title = item.scheduleTitle,
                    isEnabled = item.hasActiveAlarm,
                    isRepeat = item.isRepeat,
                    repeatDays = item.repeatDays,
                    onToggleClick = { onClickSwitch(item.scheduleId, it) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray600)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            AlarmInfoSection(
                preparationAlarm = item.preparationAlarm,
                departureAlarm = item.departureAlarm
            )
        }
    }
}

@Composable
fun TopFogOverlay(
    modifier: Modifier = Modifier,
    blurRadius: Dp = 22.dp,
    cornerRadius: Dp = 12.dp,
    leftFogColor: Color = Gray500,
    rightGlowColor: Color,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithCache {
                val w = size.width
                val h = size.height

                // 좌상단 회색 안개
                val leftFog = Brush.linearGradient(
                    colors = listOf(
                        leftFogColor,
                        Gray700,
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w * 0.55f, h * 1.25f)
                )

                // 우상단 초록 안개
                val rightGlow = Brush.radialGradient(
                    colors = listOf(
                        rightGlowColor.copy(alpha = 0.2f),
                        Gray700.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.92f, -h * 0.25f),
                    radius = w * 0.85f
                )

                // 화이트 헤이즈
                val whiteHaze = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = h
                )

                val fadeMask = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to Color.White,
                        0.65f to Color.White.copy(alpha = 0.70f),
                        1.00f to Color.Transparent
                    ),
                    startY = 0f,
                    endY = h
                )

                onDrawBehind {
                    drawRect(leftFog)
                    drawRect(rightGlow)
                    drawRect(whiteHaze)
                    drawRect(fadeMask, blendMode = BlendMode.DstIn)
                }
            }
            .blur(blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    )
}

@Composable
private fun ScheduleInfoToggleSection(
    date: String,
    title: String,
    isEnabled: Boolean,
    isRepeat: Boolean,
    repeatDays: List<Int>,
    onToggleClick: (Boolean) -> Unit
) {
    val formattedDate = DateTimeFormatter.formatDateWithDayOfWeek(date)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            if (isRepeat) {
                val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dayLabels.forEachIndexed { index, label ->
                        val isActive = repeatDays.contains(index + 1)
                        OnDotText(
                            text = label,
                            style = OnDotTextStyle.BodySmallR1,
                            color = if (isActive) Green500 else Gray400,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = (1.5).dp)
                        )
                    }
                }
            } else {
                OnDotText(text = formattedDate, style = OnDotTextStyle.BodySmallR1, color = Green500)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val appointmentTime = HomeUiState.appointmentAtTime(date)

                OnDotText(
                    text = appointmentTime(appointmentTime),
                    style = OnDotTextStyle.BodyLargeR1,
                    color = Gray0
                )

                OnDotText(
                    text = " | $title",
                    style = OnDotTextStyle.BodyLargeR1,
                    color = Gray200,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        OnDotSwitch(
            checked = isEnabled,
            onClick = onToggleClick
        )
    }
}

@Composable
private fun AlarmInfoSection(
    preparationAlarm: Alarm,
    departureAlarm: Alarm
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AlarmItem(type = AlarmType.Preparation, alarm = preparationAlarm)
        Spacer(modifier = Modifier.weight(1f))
        AlarmItem(type = AlarmType.Departure, alarm = departureAlarm)
    }
}

@Composable
private fun AlarmItem(
    type: AlarmType,
    alarm: Alarm
) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        val leadingText = when(type) {
            AlarmType.Departure -> WORD_DEPARTURE
            AlarmType.Preparation -> WORD_PREPARATION
        }
        val hourText = DateTimeFormatter.formatHourMinute(alarm.triggeredAt)
        val fontColor = if (alarm.enabled) Gray0 else Gray400

        OnDotText(
            text = leadingText,
            style = OnDotTextStyle.TitleMediumR,
            color = fontColor,
            modifier = Modifier.alignBy(FirstBaseline)
        )
        Spacer(Modifier.width(8.dp))
        OnDotText(
            text = hourText,
            style = OnDotTextStyle.TitleLargeR,
            color = fontColor,
            modifier = Modifier.alignBy(FirstBaseline)
        )
    }
}

/**-----------------------Legacy----------------------*/

@Composable
private fun ScheduleTitleDateRow(
    title: String,
    date: String,
    isRepeat: Boolean,
    repeatDays: List<Int>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = title,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray200
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isRepeat) {
            val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                dayLabels.forEachIndexed { index, label ->
                    val isActive = repeatDays.contains(index + 1)
                    OnDotText(
                        text = label,
                        style = OnDotTextStyle.BodySmallR1,
                        color = if (isActive) Green500 else Gray400,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = (1.5).dp)
                    )
                }
            }
        } else {
            OnDotText(
                text = DateTimeFormatter.formatDate(date, "."),
                style = OnDotTextStyle.BodySmallR1,
                color = Gray400
            )
        }
    }
}

@Composable
private fun DepartureAlarmInfoRow(
    alarm: Alarm,
    isEnabled: Boolean,
    onClickSwitch: (Boolean) -> Unit
) {
    val (period, time) = DateTimeFormatter.formatAmPmTimePair(alarm.triggeredAt)

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = period,
            style = OnDotTextStyle.TitleMediumL,
            color = Gray0,
            modifier = Modifier.alignByBaseline()
        )

        Spacer(modifier = Modifier.width(4.dp))

        OnDotText(
            text = time,
            style = OnDotTextStyle.TitleLargeL,
            color = Gray0,
            modifier = Modifier.alignByBaseline()
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotSwitch(
            checked = isEnabled,
            onClick = onClickSwitch
        )
    }
}

@Composable
private fun PreparationAlarmText(
    alarm: Alarm
) {
    if (alarm.enabled) {
        OnDotText(
            text = "준비시작 ${DateTimeFormatter.formatAmPmTime(alarm.triggeredAt)}",
            style = OnDotTextStyle.BodyLargeR2,
            color = Gray200
        )
    } else {
        OnDotText(
            text = EMPTY_PREPARATION_ALARM,
            style = OnDotTextStyle.BodyLargeR2,
            color = Gray200
        )
    }
}