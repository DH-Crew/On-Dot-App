package com.dh.ondot.presentation.general.repeat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.constans.AppConstants
import com.dh.ondot.domain.model.enums.ChipStyle
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.RepeatType
import com.dh.ondot.presentation.ui.components.CheckTextChip
import com.dh.ondot.presentation.ui.components.OnDotSwitch
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.TextChip
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.WORD_REPEAT

@Composable
fun RepeatSettingSection(
    isRepeat: Boolean,
    activeCheckChip: Int?,
    activeWeekDays: Set<Int>,
    onClickSwitch: (Boolean) -> Unit,
    onClickCheckTextChip: (Int) -> Unit,
    onClickTextChip: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        RepeatToggleRow(isRepeat, onClickSwitch)

        if (isRepeat) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.padding(horizontal = 4.dp))

            Spacer(modifier = Modifier.height(16.dp))

            RepeatTypeChips(
                activeCheckChip = activeCheckChip,
                activeWeekDays = activeWeekDays,
                onClickCheckTextChip = onClickCheckTextChip
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeekDayChips(
                activeWeekDays = activeWeekDays,
                onClickTextChip = onClickTextChip
            )
        }
    }
}

@Composable
fun RepeatToggleRow(
    isRepeat: Boolean,
    onClickSwitch: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = WORD_REPEAT,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotSwitch(
            checked = isRepeat,
            onClick = onClickSwitch
        )
    }
}

@Composable
fun RepeatTypeChips(
    activeCheckChip: Int?,
    activeWeekDays: Set<Int>,
    onClickCheckTextChip: (Int) -> Unit
) {
    ChipsRow {
        RepeatType.entries.forEach { type ->
            CheckTextChip(
                text = type.title,
                chipStyle = if (activeCheckChip == null && activeWeekDays.isEmpty()) ChipStyle.Normal
                else {
                    if (activeCheckChip == type.index) ChipStyle.Active
                    else ChipStyle.Inactive
                },
                onClick = { onClickCheckTextChip(type.index) }
            )
        }
    }
}

@Composable
fun WeekDayChips(
    activeWeekDays: Set<Int>,
    onClickTextChip: (Int) -> Unit
) {
    ChipsRow {
        AppConstants.weekDays.forEachIndexed { i, day ->
            TextChip(
                text = day,
                chipStyle = if (activeWeekDays.isEmpty()) ChipStyle.Normal
                else {
                    if (activeWeekDays.contains(i)) ChipStyle.Active
                    else ChipStyle.Inactive
                },
                onClick = { onClickTextChip(i) }
            )
        }
    }
}

@Composable
private fun ChipsRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}