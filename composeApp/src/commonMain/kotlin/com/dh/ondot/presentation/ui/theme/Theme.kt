package com.dh.ondot.presentation.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.dh.ondot.presentation.ui.components.LocalOnDotTypography

@Composable
fun OnDotTheme(content: @Composable () -> Unit) {
    val typography = OnDotTypo()

    CompositionLocalProvider(
        LocalOnDotTypography provides typography
    ) {
        content()
    }
}
