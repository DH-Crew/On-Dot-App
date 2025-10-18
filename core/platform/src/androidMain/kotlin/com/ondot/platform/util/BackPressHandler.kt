package com.ondot.platform.util

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
}