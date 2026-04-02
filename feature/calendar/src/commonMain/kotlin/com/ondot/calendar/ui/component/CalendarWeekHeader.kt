package com.ondot.calendar.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray100
import com.ondot.designsystem.theme.OnDotColor.Red
import com.ondot.domain.model.constants.AppConstants
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun CalendarWeekHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppConstants.weekDays.forEachIndexed { index, label ->
            val color = if (index == 0) Red else Gray100

            Box(
                modifier =
                    Modifier
                        .height(28.dp)
                        .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                OnDotText(
                    text = label,
                    style = OnDotTextStyle.BodyLargeR2,
                    color = color,
                )
            }
        }
    }
}
