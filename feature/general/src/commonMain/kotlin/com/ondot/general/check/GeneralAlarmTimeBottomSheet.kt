package com.ondot.general.check

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.dh.ondot.presentation.ui.theme.WORD_COMPETE
import com.ondot.designsystem.components.Calendar
import com.ondot.designsystem.components.DateSectionHeader
import com.ondot.designsystem.components.OnDotBottomSheet
import com.ondot.designsystem.components.OnDotButton
import com.ondot.designsystem.components.TimePicker
import com.ondot.designsystem.components.TimeSectionHeader
import com.ondot.designsystem.theme.OnDotColor.Gray600
import com.ondot.domain.model.enums.ButtonType
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GeneralAlarmTimeBottomSheet(
    currentTime: LocalTime,
    currentDate: LocalDate,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalDate, LocalTime) -> Unit,
) {
    var selectedTime by remember(currentTime) { mutableStateOf(currentTime) }
    var selectedDate by remember(currentDate) { mutableStateOf(currentDate) }

    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = currentTime.hour.coerceIn(0, 23))
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = currentTime.minute.coerceIn(0, 59))

    OnDotBottomSheet(
        onDismiss = onDismiss,
        contentPaddingTop = 32.dp,
        contentPaddingBottom = 16.dp,
        content = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                TimeSectionHeader(
                    selectedTime = selectedTime,
                    isActiveDial = true,
                    onToggleDial = {},
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

                TimePicker(
                    periodState = periodState,
                    hourState = hourState,
                    minuteState = minuteState,
                    onTimeSelected = { selectedTime = it },
                )

                Spacer(modifier = Modifier.height(26.dp))

                OnDotButton(
                    buttonText = WORD_COMPETE,
                    buttonType = ButtonType.Green500,
                    onClick = {
                        onTimeSelected(selectedDate, selectedTime)
                    },
                )

                Spacer(Modifier.height(12.dp))
            }
        },
    )
}

@Preview
@Composable
private fun Preview() {
    OnDotTheme {
        GeneralAlarmTimeBottomSheet(
            currentTime = LocalTime(12, 0),
            currentDate = LocalDate(2023, 1, 1),
            onDismiss = {},
            onTimeSelected = { _, _ -> },
        )
    }
}
