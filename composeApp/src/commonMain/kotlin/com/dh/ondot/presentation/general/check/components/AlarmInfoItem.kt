package com.dh.ondot.presentation.general.check.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.AlarmType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.DEPARTURE_ALARM_LABEL
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray50
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.PREPARATION_ALARM_LABEL

@Composable
fun AlarmInfoItem(
    info: AlarmDetail,
    type: AlarmType,
) {
    val (period, time) = DateTimeFormatter.formatAmPmTimePair(info.triggeredAt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnDotText(
            text = if (type == AlarmType.Departure) DEPARTURE_ALARM_LABEL else PREPARATION_ALARM_LABEL,
            style = OnDotTextStyle.BodyLargeR2,
            color = Gray50
        )

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
        }
    }
}