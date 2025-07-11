package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.util.DateTimeFormatter.formatAmPmTime
import com.dh.ondot.core.util.DateTimeFormatter.formatKorean
import com.dh.ondot.domain.model.enums.ChipStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green900
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Composable
fun DateTimeInfoBar(
    date: LocalDate,
    time: LocalTime,
    backgroundColor: Color = Green900
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextChip(
            text = date.formatKorean(),
            chipStyle = ChipStyle.Info,
        )

        Spacer(modifier = Modifier.weight(1f))

        TextChip(
            text = time.formatAmPmTime(),
            chipStyle = ChipStyle.Info
        )
    }
}