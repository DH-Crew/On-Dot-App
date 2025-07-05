package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotTypo

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    maxLength: Int = 5,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number
    )
) {
    BasicTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength) onValueChange(it)
        },
        enabled = enabled,
        singleLine = true,
        textStyle = OnDotTypo().bodyLargeR1.copy(color = OnDotColor.Gray0),
        keyboardOptions = keyboardOptions,
        cursorBrush = SolidColor(OnDotColor.Gray0),
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier
                    .background(color = OnDotColor.Gray700, shape = RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = OnDotColor.Gray600,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .defaultMinSize(minWidth = 64.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    OnDotText(
                        text = placeholder,
                        color = OnDotColor.Gray300,
                        style = OnDotTextStyle.BodyLargeR1,
                        maxLines = maxLines
                    )
                }
                innerTextField()
            }
        }
    )
}