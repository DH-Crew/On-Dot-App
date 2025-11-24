package com.ondot.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray50
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green1000
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green800
import com.ondot.domain.model.enums.ChipStyle
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_check_green
import org.jetbrains.compose.resources.painterResource

@Composable
fun TextChip(
    text: String,
    chipStyle: ChipStyle,
    onClick: () -> Unit = {}
) {
    val fontColor = when (chipStyle) {
        ChipStyle.Active -> Green500
        ChipStyle.Normal, ChipStyle.Info -> Gray50
        ChipStyle.Inactive -> Gray400
        ChipStyle.Yesterday -> Green1000
    }
    val backgroundColor = when (chipStyle) {
        ChipStyle.Active, ChipStyle.Normal, ChipStyle.Inactive -> Gray500
        ChipStyle.Info -> Green800
        ChipStyle.Yesterday -> Green600
    }
    val fontStyle = when(chipStyle) {
        ChipStyle.Yesterday -> OnDotTextStyle.BodyMediumSB
        else -> OnDotTextStyle.BodyMediumR
    }

    OnDotText(
        text = text,
        style = fontStyle,
        color = fontColor,
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = (6.5).dp, vertical = 4.dp)
    )
}

@Composable
fun CheckTextChip(
    text: String,
    chipStyle: ChipStyle,
    onClick: () -> Unit
) {
    val fontColor = when (chipStyle) {
        ChipStyle.Active -> Green500
        ChipStyle.Normal, ChipStyle.Info -> Gray50
        ChipStyle.Inactive -> Gray400
        else -> Gray50 // 체크 칩에는 없읍
    }
    val backgroundColor = when (chipStyle) {
        ChipStyle.Active, ChipStyle.Normal, ChipStyle.Inactive -> Gray500
        ChipStyle.Info -> Green800
        else -> Gray50 // 체크 칩에는 없음
    }

    Row(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnDotText(
            text = text,
            style = OnDotTextStyle.BodyMediumR,
            color = fontColor,
            modifier = Modifier
                .background(backgroundColor, shape = RoundedCornerShape(6.dp))
                .padding(horizontal = (6.5).dp, vertical = 3.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Image(
            painter = painterResource(Res.drawable.ic_check_green),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp),
            colorFilter = ColorFilter.tint(fontColor)
        )
    }
}