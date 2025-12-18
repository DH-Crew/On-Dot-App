package com.ondot.onboarding.step

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.ondot.design_system.components.OnDotHighlightText
import com.ondot.design_system.components.OnDotText
import com.ondot.design_system.components.OnboardingAnswerList
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.ui.UserAnswer
import com.ondot.util.AnalyticsLogger

@Deprecated("이제 사용하지 않는 화면")
@Composable
fun OnboardingStep4(
    answerList: List<UserAnswer>,
    selectedAnswerIndex: Int,
    interactionSource: MutableInteractionSource,
    onClickAnswer: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnDotHighlightText(
            text = ONBOARDING4_TITLE,
            highlight = ONBOARDING4_TITLE_HIGHLIGHT,
            style = OnDotTextStyle.TitleMediumM,
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(
            text = ONBOARDING4_SUB_TITLE,
            style = OnDotTextStyle.BodyMediumR,
            color = OnDotColor.Green300
        )

        Spacer(modifier = Modifier.height(40.dp))

        OnboardingAnswerList(
            answerList = answerList,
            selectedAnswerIndex = selectedAnswerIndex,
            interactionSource = interactionSource,
            onClickAnswer = onClickAnswer
        )
    }
}