package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green800
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green900
import com.ondot.domain.model.constants.AppConstants
import com.ondot.domain.model.enums.ChipStyle
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.util.DateTimeFormatter.formatAmPmTime
import com.ondot.util.DateTimeFormatter.formatKorean
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun DateTimeInfoBar(
    repeatDays: List<Int> = emptyList(),
    date: LocalDate?,
    time: LocalTime,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backgroundColor: Color = Green900,
    onClickDate: () -> Unit = {},
    onClickTime: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (date != null) {
            TextChip(
                text = date.formatKorean(),
                chipStyle = ChipStyle.Info,
                onClick = onClickDate
            )
        } else {
            Row(
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClickDate
                )
            ) {
                AppConstants.weekDays.forEachIndexed { index, day ->
                    val isSelected = repeatDays.contains(index)

                    OnDotText(
                        text = day,
                        style = OnDotTextStyle.BodyMediumR,
                        color = if (isSelected) Gray0 else Green800,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextChip(
            text = time.formatAmPmTime(),
            chipStyle = ChipStyle.Info,
            onClick = onClickTime
        )
    }
}