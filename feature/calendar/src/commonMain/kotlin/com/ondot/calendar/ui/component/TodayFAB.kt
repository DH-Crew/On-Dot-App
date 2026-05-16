package com.ondot.calendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.WORD_TODAY
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun TodayFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .width(52.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(Gray0)
                .border(1.dp, Gray300, RoundedCornerShape(99.dp))
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        OnDotText(
            text = WORD_TODAY,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray900,
        )
    }
}
