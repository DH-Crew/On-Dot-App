package com.ondot.platform.util

import androidx.compose.runtime.Composable

@Composable
actual fun BackPressHandler(onBack: () -> Unit) {} // Ios에서는 무시