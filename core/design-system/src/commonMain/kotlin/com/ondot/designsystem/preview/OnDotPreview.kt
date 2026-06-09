package com.ondot.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotTheme
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray900
import com.ondot.domain.model.enums.OnDotTextStyle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun OnDotTextPreview() {
    OnDotTheme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Gray0)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            OnDotText(
                text = "OnDot Preview",
                style = OnDotTextStyle.BodyLargeSB,
                color = Gray900,
            )
        }
    }
}
