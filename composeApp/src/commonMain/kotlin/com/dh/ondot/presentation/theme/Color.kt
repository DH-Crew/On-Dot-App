package com.dh.ondot.presentation.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object OnDotColor {
    // Green scale
    val Green50 = Color(0xFFF9FFE6)
    val Green100 = Color(0xFFEBFFB0)
    val Green200 = Color(0xFFE2FF8A)
    val Green300 = Color(0xFFD4FF54)
    val Green400 = Color(0xFFCCFF33)
    val Green500 = Color(0xFFBFFF00)
    val Green600 = Color(0xFFAEE800)
    val Green700 = Color(0xFF88B500)
    val Green800 = Color(0xFF698C00)
    val Green900 = Color(0xFF506B00)
    val Green1000 = Color(0xFF374A00)

    // Gray scale
    val Gray0 = Color(0xFFFFFFFF)
    val Gray50 = Color(0xFFECECEC)
    val Gray100 = Color(0xFFC4C4C4)
    val Gray200 = Color(0xFFA7A7A7)
    val Gray300 = Color(0xFF7F7F7F)
    val Gray400 = Color(0xFF666666)
    val Gray500 = Color(0xFF404040)
    val Gray600 = Color(0xFF3A3A3A)
    val Gray700 = Color(0xFF2D2D2D)
    val Gray800 = Color(0xFF232323)
    val Gray900 = Color(0xFF1B1B1B)

    // Red
    val Red = Color(0xFFFF196D)

    // Gradients
    val Gradient = Brush.linearGradient(
        colors = listOf(Color(0xFFBFFF00).copy(alpha = 0.5f), Color(0xFFFF196D)),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val GradientGreenTop = Brush.verticalGradient(
        colors = listOf(Color(0xFF88B500), Color(0xFFA4D01F))
    )

    val GradientGreenBottom = Brush.verticalGradient(
        colors = listOf(Color(0xFFA4D01F), Color(0xFFD4FF54))
    )

    val GradientLogin = Brush.verticalGradient(
        colors = listOf(Color(0xFF1B1B1B), Color(0xFF2A340D))
    )
}