package com.ondot.calendar.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ondot.calendar.contract.CalendarMonth
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.ui.util.noRippleClickable
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_arrow_left_small
import ondot.core.design_system.generated.resources.ic_arrow_right_small
import org.jetbrains.compose.resources.painterResource

@Composable
fun CalendarTopBar(
    modifier: Modifier = Modifier,
    currentMonth: CalendarMonth,
    onClickPreviousMonth: () -> Unit,
    onClickNextMonth: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Gray900),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OnDotText(
            text = "${currentMonth.year}. ${currentMonth.month.toString().padStart(2, '0')}",
            style = OnDotTextStyle.TitleMediumSB,
            color = Gray0,
            modifier =
                Modifier
                    .weight(1f),
        )

        Image(
            painter = painterResource(Res.drawable.ic_arrow_left_small),
            contentDescription = null,
            modifier =
                Modifier
                    .size(39.dp)
                    .noRippleClickable(onClick = onClickPreviousMonth),
        )

        Image(
            painter = painterResource(Res.drawable.ic_arrow_right_small),
            contentDescription = null,
            modifier =
                Modifier
                    .size(39.dp)
                    .noRippleClickable(onClick = onClickNextMonth),
        )
    }
}
