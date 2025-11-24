package com.ondot.main.home.components

import androidx.compose.runtime.Composable
import com.dh.ondot.presentation.ui.theme.ALARM_IMMINENT
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.ondot.design_system.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun RemainingTimeText(
    day: Int,
    hour: Int,
    minute: Int
) {
    if (day == 0 && hour == 0 && minute == 0) {
        OnDotText(
            text = ALARM_IMMINENT,
            style = OnDotTextStyle.TitleMediumSB,
            color = Gray0
        )
    } else {
        // String.format은 JVM 전용이라 commonMain에서 사용할 수 없다고 한다. (2025/07/07)
        OnDotText(
            text = "${day}일 ${hour}시간 ${minute}분 후에 울려요",
            style = OnDotTextStyle.TitleMediumSB,
            color = Gray0
        )
    }
}