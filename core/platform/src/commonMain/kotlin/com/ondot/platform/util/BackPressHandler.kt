package com.ondot.platform.util

import androidx.compose.runtime.Composable

@Composable
expect fun BackPressHandler(onBack: () -> Unit)