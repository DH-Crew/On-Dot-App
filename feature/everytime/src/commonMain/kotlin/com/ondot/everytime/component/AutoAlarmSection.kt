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
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_TITLE1
import com.dh.ondot.presentation.ui.theme.AUTO_ALARM_SECTION_TITLE2
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.ondot.designsystem.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.everytime.BodyText
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
        OnDotText(
            text = AUTO_ALARM_SECTION_TITLE1,
            color = Green500,
            style = OnDotTextStyle.TitleMediumSB,
        )

        Spacer(Modifier.height(4.dp))

        OnDotText(
            text = AUTO_ALARM_SECTION_TITLE2,
            color = Gray0,
            style = OnDotTextStyle.TitleMediumSB,
        )

        Spacer(Modifier.height(16.dp))

        BodyText(
            text = AUTO_ALARM_SECTION_CONTENT1,
        )

        Spacer(Modifier.height(16.dp))

        BodyText(
            text = AUTO_ALARM_SECTION_CONTENT2,
            emphasize = true,
        )

        Spacer(Modifier.height(40.dp))

        SchedulePreviewCard()
    }
}

@Composable
private fun SchedulePreviewCard() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
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
