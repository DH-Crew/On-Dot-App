package com.ondot.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotTypo
import com.ondot.domain.model.enums.OnDotTextStyle

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = OnDotColor.Gray700,
    placeholder: String = "",
    enabled: Boolean = true,
    maxLength: Int = 5,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    onClickWhenReadOnly: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentAlignment: Alignment = Alignment.CenterStart
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = OnDotColor.Gray600,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .defaultMinSize(minWidth = 64.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    if (readOnly) {
                        onClickWhenReadOnly()
                    } else {
                        focusRequester.requestFocus()
                    }
                }
            ),
        contentAlignment = contentAlignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                it.invoke()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (value.isEmpty()) {
                    OnDotText(
                        text = placeholder,
                        color = OnDotColor.Gray300,
                        style = OnDotTextStyle.BodyLargeR1,
                        maxLines = maxLines
                    )
                }

                BasicTextField(
                    value = value,
                    onValueChange = {
                        if (it.length <= maxLength) onValueChange(it)
                    },
                    enabled = enabled,
                    singleLine = singleLine,
                    textStyle = OnDotTypo().bodyLargeR1.copy(color = OnDotColor.Gray0),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    cursorBrush = SolidColor(OnDotColor.Gray0),
                    readOnly = readOnly,
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                it.invoke()
            }
        }
    }
}