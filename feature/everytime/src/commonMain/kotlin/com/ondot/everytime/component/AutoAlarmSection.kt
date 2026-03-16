package com.ondot.everytime.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_TITLE
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_TITLE_HIGHLIGHT
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_schedule_preview_card
import org.jetbrains.compose.resources.painterResource

@Composable
fun AutoAlarmSection() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SchedulePreviewCard()

        Spacer(Modifier.height(28.dp))

        OnDotHighlightText(
            text = AUTO_ALARM_SECTION_TITLE,
            textAlign = TextAlign.Center,
            highlight = AUTO_ALARM_SECTION_TITLE_HIGHLIGHT,
            highlightColor = Green500,
            style = OnDotTextStyle.TitleMediumSB,
        )
    }
}

@Composable
private fun SchedulePreviewCard() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec =
            infiniteRepeatable(
                tween(2200, easing = EaseInOut),
                RepeatMode.Reverse,
            ),
    )

    Image(
        painter = painterResource(Res.drawable.ic_schedule_preview_card),
        contentDescription = null,
        modifier =
            Modifier
                .padding(horizontal = 50.dp)
                .fillMaxWidth()
                .aspectRatio(293f / 128f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
    )
}
