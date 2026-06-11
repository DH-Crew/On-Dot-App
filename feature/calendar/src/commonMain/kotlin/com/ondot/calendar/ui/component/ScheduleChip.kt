package com.ondot.calendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.designsystem.theme.OnDotColor.Green50
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.designsystem.theme.OnDotColor.Green800
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun ScheduleChip(
    text: String,
    isRepeat: Boolean,
    hasActiveAlarm: Boolean,
) {
    val backgroundColor =
        when {
            !hasActiveAlarm -> Gray900
            isRepeat -> Green500
            else -> Green50
        }
    val textColor =
        when {
            !hasActiveAlarm -> Gray300
            isRepeat -> Gray900
            else -> Green800
        }
    val borderColor =
        when {
            !hasActiveAlarm -> Gray300
            isRepeat -> Green500
            else -> Green50
        }
    val shape = RoundedCornerShape(2.dp)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(13.dp)
                .clip(shape)
                .background(backgroundColor)
                .border(1.dp, borderColor, shape)
                .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        OnDotText(
            text = text,
            style = OnDotTextStyle.BodySmallM,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
