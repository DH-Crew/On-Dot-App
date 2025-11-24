package com.ondot.design_system.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_checked
import ondot.core.design_system.generated.resources.ic_unchecked
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnDotCheckBox(
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Image(
        painter = painterResource(if (isChecked) Res.drawable.ic_checked else Res.drawable.ic_unchecked),
        contentDescription = null,
        modifier = Modifier
            .size(23.dp)
            .clickable { onCheckedChange() }
    )
}