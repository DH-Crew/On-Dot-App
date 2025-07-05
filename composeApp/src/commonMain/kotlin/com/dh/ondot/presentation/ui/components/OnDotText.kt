package com.dh.ondot.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import com.dh.ondot.core.ui.extensions.toTextStyle
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor

@Composable
fun OnDotText(
    text: String,
    style: OnDotTextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val textStyle = style.toTextStyle()

    CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) {
        Text(
            text = text,
            style = textStyle,
            color = color,
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow
        )
    }
}

@Composable
fun OnDotHighlightText(
    text: String,
    textColor: Color = OnDotColor.Gray0,
    highlight: String,
    highlightColor: Color = OnDotColor.Green500,
    style: OnDotTextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val textStyle = style.toTextStyle()

    val annotatedText = buildAnnotatedString {
        val startIndex = text.indexOf(highlight)

        if (startIndex >= 0) {
            withStyle(SpanStyle(color = textColor)) {
                append(text.substring(0, startIndex))
            }

            withStyle(SpanStyle(color = highlightColor)) {
                append(highlight)
            }

            withStyle(SpanStyle(color = textColor)) {
                append(text.substring(startIndex + highlight.length))
            }
        } else {
            withStyle(SpanStyle(color = textColor)) {
                append(text)
            }
        }
    }

    CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) {
        Text(
            text = annotatedText,
            style = textStyle,
            color = Color.Unspecified,
            modifier = modifier,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = overflow
        )
    }
}
