package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green600
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.ui.UserAnswer

@Composable
fun OnboardingAnswerList(
    answerList: List<UserAnswer>,
    selectedAnswerIndex: Int,
    interactionSource: MutableInteractionSource,
    onClickAnswer: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        answerList.forEachIndexed { index, item ->
            OnboardingAnswerItem(
                item = item,
                selected = index == selectedAnswerIndex,
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onClickAnswer(index) }
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OnboardingAnswerItem(
    item: UserAnswer,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Gray700, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = if (selected) Green600 else Gray600, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        OnDotText(
            text = item.content,
            style = OnDotTextStyle.BodyMediumR,
            color = Gray0
        )
    }
}