package com.ondot.design_system.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_HOUR_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.ONBOARDING1_MINUTE_PLACEHOLDER
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.WORD_HOUR
import com.dh.ondot.presentation.ui.theme.WORD_MINUTE
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun HourMinuteTextField(
    hourInput: String,
    minuteInput: String,
    onHourInputChanged: (String) -> Unit,
    onMinuteInputChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedTextField(
            value = hourInput,
            onValueChange = onHourInputChanged,
            placeholder = ONBOARDING1_HOUR_PLACEHOLDER,
            maxLength = 1,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )

        OnDotText(
            text = WORD_HOUR,
            style = OnDotTextStyle.BodyLargeR1,
            color = OnDotColor.Gray200
        )

        RoundedTextField(
            value = minuteInput,
            onValueChange = onMinuteInputChanged,
            placeholder = ONBOARDING1_MINUTE_PLACEHOLDER,
            maxLength = 2,
            modifier = Modifier.weight(1f).padding(start = 11.dp, end = 8.dp)
        )

        OnDotText(
            text = WORD_MINUTE,
            style = OnDotTextStyle.BodyLargeR1,
            color = OnDotColor.Gray200
        )
    }
}