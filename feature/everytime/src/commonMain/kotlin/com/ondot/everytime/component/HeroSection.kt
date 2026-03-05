package com.ondot.everytime.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_CONTENT3
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_TITLE1
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_TITLE2
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.HighlightRed
import com.dh.ondot.presentation.ui.theme.WORD_EVERYTIME
import com.ondot.design_system.components.OnDotHighlightText
import com.ondot.design_system.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.everytime.BodyText
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_landing_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .padding(26.dp)
                .offset(y = 50.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                HighlightRed.copy(alpha = 0.3f),
                                HighlightRed.copy(alpha = 0.0f)
                            )
                        ),
                        radius = size.maxDimension,
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OnDotHighlightText(
                highlight = WORD_EVERYTIME,
                highlightColor = HighlightRed,
                text = HERO_SECTION_TITLE1,
                style = OnDotTextStyle.TitleMediumSB,
            )

            Spacer(Modifier.height(4.dp))

            OnDotText(
                text = HERO_SECTION_TITLE2,
                style = OnDotTextStyle.TitleMediumSB,
                color = Gray0,
            )

            Spacer(Modifier.height(16.dp))

            BodyText(
                text = HERO_SECTION_CONTENT1
            )

            Spacer(Modifier.height(16.dp))

            BodyText(
                text = HERO_SECTION_CONTENT2,
            )

            Spacer(Modifier.height(16.dp))

            BodyText(
                text = HERO_SECTION_CONTENT3,
                emphasize = true,
            )

            Spacer(Modifier.height(40.dp))

            FloatingLogo()
        }
    }
}

@Composable
private fun FloatingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Image(
        painter = painterResource(Res.drawable.ic_landing_logo),
        contentDescription = null,
        modifier =
            Modifier
                .size(120.dp)
                .offset(y = offsetY.dp)
    )
}
