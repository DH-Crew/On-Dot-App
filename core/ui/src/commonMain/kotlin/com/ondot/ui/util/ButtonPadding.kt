package com.ondot.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.ondot.designsystem.getPlatform

@Composable
fun Modifier.buttonPadding(): Modifier = padding(bottom = if (getPlatform() == ANDROID) 16.dp else 37.dp)
