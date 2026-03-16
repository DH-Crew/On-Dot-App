package com.ondot.everytime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT3
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_CONTENT4
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION1
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION2
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION3
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_DESCRIPTION4
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_TITLE1
import com.dh.ondot.presentation.ui.theme.BENEFITS_SECTION_TITLE1_HIGHLIGHT
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Green500
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_landing_alarm
import ondot.core.design_system.generated.resources.ic_landing_bus
import ondot.core.design_system.generated.resources.ic_landing_repeat
import ondot.core.design_system.generated.resources.ic_landing_shower
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BenefitsSection() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnDotHighlightText(
            highlight = BENEFITS_SECTION_TITLE1_HIGHLIGHT,
            highlightColor = Green500,
            text = BENEFITS_SECTION_TITLE1,
            style = OnDotTextStyle.TitleMediumSB,
        )

        Spacer(Modifier.height(40.dp))

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

        Spacer(Modifier.height(16.dp))

        BenefitItem(
            icon = Res.drawable.ic_landing_repeat,
            title = BENEFITS_SECTION_CONTENT4,
            description = BENEFITS_SECTION_DESCRIPTION4,
        )
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
