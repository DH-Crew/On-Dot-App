package com.dh.ondot.presentation.home.components

import androidx.compose.runtime.Composable
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0

@Composable
fun RemainingTimeText(
    day: Int,
    hour: Int,
    minute: Int
) {
    // String.format은 JVM 전용이라 commonMain에서 사용할 수 없다고 한다. (2025/07/07)
    OnDotText(
        text = "${day}일 ${hour}시간 ${minute}분 후에 울려요",
        style = OnDotTextStyle.TitleMediumSB,
        color = Gray0
    )
}