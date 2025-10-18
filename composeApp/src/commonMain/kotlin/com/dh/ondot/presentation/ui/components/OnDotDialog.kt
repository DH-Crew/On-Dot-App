package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.WORD_CANCEL
import com.dh.ondot.presentation.ui.theme.WORD_CONFIRM
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.DialogType
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun OnDotDialog(
    dialogType: DialogType = DialogType.TWO,
    dialogTitle: String = "",
    dialogContent: String = "",
    positiveText: String = WORD_CONFIRM,
    negativeText: String = WORD_CANCEL,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900.copy(alpha = 0.8f))
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onDismiss
            )
            .padding(horizontal = 52.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray600, RoundedCornerShape(12.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnDotText(text = dialogTitle, style = OnDotTextStyle.TitleSmallSB, color = Gray0)

            Spacer(modifier = Modifier.height(8.dp))

            OnDotText(text = dialogContent, style = OnDotTextStyle.BodyMediumR, color = Gray0, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(16.dp))

            when (dialogType) {
                DialogType.ONE -> TODO()
                DialogType.TWO -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OnDotButton(
                            buttonText = negativeText,
                            buttonType = ButtonType.Gray400,
                            buttonHeight = 42.dp,
                            onClick = onNegativeClick,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OnDotButton(
                            buttonText = positiveText,
                            buttonType = ButtonType.Red,
                            buttonHeight = 42.dp,
                            onClick = onPositiveClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}