package com.dh.ondot.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.presentation.ui.components.OnDotSwitch
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.EMPTY_PREPARATION_ALARM
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500

@Composable
fun ScheduleList(
    scheduleList: List<Schedule>,
    onClickSwitch: (Long, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(scheduleList) {
            ScheduleListItem(
                item = it,
                onClickSwitch = onClickSwitch
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ScheduleListItem(
    item: Schedule,
    onClickSwitch: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Gray700, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        ScheduleTitleDateRow(
            title = item.scheduleTitle,
            date = item.appointmentAt,
            isRepeat = item.isRepeat,
            repeatDays = item.repeatDays
        )

        DepartureAlarmInfoRow(
            alarmDetail = item.departureAlarm,
            isEnabled = item.isEnabled,
            onClickSwitch = {
                onClickSwitch(item.scheduleId, it)
            }
        )

        PreparationAlarmText(item.preparationAlarm)
    }
}

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
    alarmDetail: AlarmDetail,
    isEnabled: Boolean,
    onClickSwitch: (Boolean) -> Unit
) {
    val (period, time) = DateTimeFormatter.formatAmPmTimePair(alarmDetail.triggeredAt)

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
    alarmDetail: AlarmDetail
) {
    if (alarmDetail.enabled == true) {
        OnDotText(
            text = "준비시작 ${DateTimeFormatter.formatAmPmTime(alarmDetail.triggeredAt)}",
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