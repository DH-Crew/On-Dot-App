package com.dh.ondot.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_free
import ondot.composeapp.generated.resources.ic_ondot
import org.jetbrains.compose.resources.painterResource

@Composable
fun UserBadgeBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_ondot),
            contentDescription = null,
            modifier = Modifier.size(width = 103.dp, height = 20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Image(
            painter = painterResource(Res.drawable.ic_free),
            contentDescription = null,
            modifier = Modifier.size(width = 38.dp, height = 19.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}