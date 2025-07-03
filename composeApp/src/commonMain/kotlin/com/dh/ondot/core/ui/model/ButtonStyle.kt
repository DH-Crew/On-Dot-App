package com.dh.ondot.core.ui.model

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class ButtonStyle(
    val fontColor: Color,
    val backgroundColor: Color,
    val drawableResource: DrawableResource? = null
)
