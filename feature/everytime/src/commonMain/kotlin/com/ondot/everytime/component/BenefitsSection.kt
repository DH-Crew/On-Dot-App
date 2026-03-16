package com.ondot.everytime.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT3
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION1
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION2
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION3
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_TITLE
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_TITLE_HIGHLIGHT
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.HighlightRed
import com.ondot.designsystem.theme.OnDotColor.Red
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_landing_alarm
import ondot.core.design_system.generated.resources.ic_landing_bus
import ondot.core.design_system.generated.resources.ic_landing_logo
import ondot.core.design_system.generated.resources.ic_landing_shower
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BenefitsSection() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            Modifier
                .matchParentSize()
                .offset(y = -(162).dp)
                .padding(horizontal = 66.dp)
                .drawBehind {
                    drawCircle(
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        HighlightRed.copy(alpha = 0.3f),
                                        HighlightRed.copy(alpha = 0.0f),
                                    ),
                            ),
                        radius = size.maxDimension,
                    )
                },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 112.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FloatingLogo()

            Spacer(Modifier.height(28.dp))

            OnDotHighlightText(
                highlight = BENEFITS_SECTION_TITLE_HIGHLIGHT,
                highlightColor = Red,
                text = BENEFITS_SECTION_TITLE,
                textAlign = TextAlign.Center,
                style = OnDotTextStyle.TitleMediumSB,
            )

            Spacer(Modifier.height(32.dp))

            BenefitItem(
                icon = Res.drawable.ic_landing_alarm,
                title = BENEFITS_SECTION_CONTENT1,
                description = BENEFITS_SECTION_DESCRIPTION1,
            )

            Spacer(Modifier.height(16.dp))

            BenefitItem(
                icon = Res.drawable.ic_landing_shower,
                title = BENEFITS_SECTION_CONTENT2,
                description = BENEFITS_SECTION_DESCRIPTION2,
            )

            Spacer(Modifier.height(16.dp))

            BenefitItem(
                icon = Res.drawable.ic_landing_bus,
                title = BENEFITS_SECTION_CONTENT3,
                description = BENEFITS_SECTION_DESCRIPTION3,
            )
        }
    }
}

@Composable
private fun BenefitItem(
    icon: DrawableResource,
    title: String,
    description: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .background(Gray700, RoundedCornerShape(12.dp))
                .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )

        Spacer(Modifier.width(16.dp))

        Column {
            OnDotText(title, color = Gray0, style = OnDotTextStyle.BodyLargeSB)
            Spacer(Modifier.height(4.dp))
            OnDotText(description, color = Gray200, style = OnDotTextStyle.BodyMediumR)
        }
    }
}

@Composable
private fun FloatingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2500, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "",
    )

    Image(
        painter = painterResource(Res.drawable.ic_landing_logo),
        contentDescription = null,
        modifier =
            Modifier
                .size(120.dp)
                .offset(y = offsetY.dp),
    )
}
