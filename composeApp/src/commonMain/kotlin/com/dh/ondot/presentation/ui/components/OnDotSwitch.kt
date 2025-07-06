package com.dh.ondot.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import kotlin.math.roundToInt

@Composable
fun OnDotSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit
) {
    val density = LocalDensity.current
    val minBound = with(density) { 0.dp.toPx() }
    val maxBound = with(density) { 22.dp.toPx() }
    val state by animateFloatAsState(
        targetValue = if (checked) maxBound else minBound,
        animationSpec = tween(durationMillis = 500),
        label = "ondot_switch"
    )

    Box(
        modifier = modifier
            .size(width = 54.dp, height = 32.dp)
            .clip(RoundedCornerShape(156.dp))
            .background(if (checked) OnDotColor.Green600 else OnDotColor.Gray400)
            .clickable(onClick = { onClick(!checked) }),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(state.roundToInt(), 0) }
                .padding(2.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Gray0)
        )
    }
}