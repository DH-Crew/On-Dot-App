package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dh.ondot.core.ui.extensions.styles
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnDotButton(
    buttonText: String,
    buttonType: ButtonType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = buttonType.styles().backgroundColor, shape = RoundedCornerShape(12.dp))
            .drawBehind {
                if (buttonType != ButtonType.Gradient) return@drawBehind

                val width = 1.dp.toPx()
                val cornerRadius = 12.dp.toPx()
                drawRoundRect(
                    brush = OnDotColor.Gradient,
                    style = Stroke(width = width),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (buttonType == ButtonType.Kakao) {
                buttonType.styles().drawableResource?.let {
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.width(21.dp).height(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
            }

            OnDotText(
                text = buttonText,
                style = OnDotTextStyle.TitleSmallSB,
                color = buttonType.styles().fontColor
            )
        }
    }
}

