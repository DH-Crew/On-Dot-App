package com.dh.ondot.presentation.onboarding.step

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.components.OnDotHighlightText
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.OnboardingAnswerList
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_SUB_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE
import com.dh.ondot.presentation.ui.theme.ONBOARDING4_TITLE_HIGHLIGHT
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.ui.UserAnswer

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