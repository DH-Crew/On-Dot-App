package com.ondot.everytime.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_TITLE
import com.dh.ondot.presentation.ui.theme.HERO_SECTION_TITLE_HIGHLIGHT
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray300
import com.ondot.designsystem.theme.OnDotColor.Red
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun HeroSection() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnDotText(
            text = HERO_SECTION_CONTENT1,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray300,
        )

        OnDotText(
            text = HERO_SECTION_CONTENT2,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray300,
        )

        Spacer(Modifier.height(12.dp))

        OnDotHighlightText(
            highlight = HERO_SECTION_TITLE_HIGHLIGHT,
            highlightColor = Red,
            text = HERO_SECTION_TITLE,
            textAlign = TextAlign.Center,
            style = OnDotTextStyle.TitleMediumSB,
        )
    }
}
