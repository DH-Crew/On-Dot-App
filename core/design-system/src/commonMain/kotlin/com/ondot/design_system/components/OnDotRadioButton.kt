package com.ondot.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray400
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green800

@Composable
fun OnDotRadioButton(
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(color = if (!selected) Gray400 else Green800),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color = Green500)
            )
        }
    }
}