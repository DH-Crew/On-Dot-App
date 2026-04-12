package com.ondot.calendar.ui.component

import androidx.compose.foundation.background
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
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun ScheduleChip(text: String) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Green500)
                .padding(horizontal = 2.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        OnDotText(
            text = text,
            style = OnDotTextStyle.BodySmallR3,
            color = Gray900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
