package com.dh.ondot.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalOnDotTypography = staticCompositionLocalOf<OnDotTypography> {
    error("OnDotTypography가 제공되지 않음")
}
val LocalOnDotColors = staticCompositionLocalOf { OnDotColor }

@Composable
fun OnDotTheme(content: @Composable () -> Unit) {
    val typography = OnDotTypo()

    CompositionLocalProvider(
        LocalOnDotTypography provides typography,
        LocalOnDotColors provides OnDotColor
    ) {
        content()
    }
}
