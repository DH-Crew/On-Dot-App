package com.ondot.everytime.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_CONTENT1
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_CONTENT2
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_CONTENT3
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_DESCRIPTION1
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_DESCRIPTION2
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_DESCRIPTION3
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_TITLE1
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_TITLE2
import com.dh.ondot.presentation.ui.theme.HOW_TO_CONNECT_SECTION_TITLE2_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray300
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.ondot.designsystem.components.OnDotHighlightText
import com.ondot.designsystem.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_landing_step1
import ondot.core.design_system.generated.resources.ic_landing_step2
import ondot.core.design_system.generated.resources.ic_landing_step3
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun HowToConnectSection(
    viewportRect: Rect?,
    minVisiblePx: Int,
    visibleMap: MutableMap<String, Boolean>,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, bottom = 94.dp)
                .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnDotText(
            text = HOW_TO_CONNECT_SECTION_TITLE1,
            color = Gray0,
            style = OnDotTextStyle.TitleMediumSB,
        )

        Spacer(Modifier.height(4.dp))

        OnDotHighlightText(
            highlight = HOW_TO_CONNECT_SECTION_TITLE2_HIGHLIGHT,
            highlightColor = Green500,
            text = HOW_TO_CONNECT_SECTION_TITLE2,
            style = OnDotTextStyle.TitleMediumSB,
        )

        Spacer(Modifier.height(40.dp))

        // StepItem은 각각 애니메이션 트래킹
        TrackableSection(
            id = "how_step_1",
            order = 1,
            viewportRect = viewportRect,
            minVisiblePx = minVisiblePx,
            visibleMap = visibleMap,
        ) {
            StepItem(
                number = 1,
                title = HOW_TO_CONNECT_SECTION_CONTENT1,
                description = HOW_TO_CONNECT_SECTION_DESCRIPTION1,
                image = Res.drawable.ic_landing_step1,
            )
        }

        Spacer(Modifier.height(40.dp))

        TrackableSection(
            id = "how_step_2",
            order = 2,
            viewportRect = viewportRect,
            minVisiblePx = minVisiblePx,
            visibleMap = visibleMap,
        ) {
            StepItem(
                number = 2,
                title = HOW_TO_CONNECT_SECTION_CONTENT2,
                description = HOW_TO_CONNECT_SECTION_DESCRIPTION2,
                image = Res.drawable.ic_landing_step2,
            )
        }

        Spacer(Modifier.height(40.dp))

        TrackableSection(
            id = "how_step_3",
            order = 3,
            viewportRect = viewportRect,
            minVisiblePx = minVisiblePx,
            visibleMap = visibleMap,
        ) {
            StepItem(
                number = 3,
                title = HOW_TO_CONNECT_SECTION_CONTENT3,
                description = HOW_TO_CONNECT_SECTION_DESCRIPTION3,
                image = Res.drawable.ic_landing_step3,
            )
        }
    }
}

@Composable
private fun StepItem(
    number: Int,
    title: String,
    image: DrawableResource,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(20.dp)
                    .background(Green600, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            OnDotText(
                text = number.toString(),
                color = Gray900,
                style = OnDotTextStyle.BodyMediumSB,
            )
        }

        Spacer(Modifier.height(6.dp))

        OnDotText(
            text = title,
            color = Gray0,
            style = OnDotTextStyle.BodyLargeSB,
        )

        Spacer(Modifier.height(4.dp))

        OnDotText(
            text = description,
            color = Gray300,
            style = OnDotTextStyle.BodyMediumR,
        )

        Spacer(Modifier.height(24.dp))

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier =
                Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
        )
    }
}
