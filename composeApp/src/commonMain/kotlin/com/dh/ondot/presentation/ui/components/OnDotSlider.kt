package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.dh.ondot.presentation.ui.theme.OnDotColor

@Composable
fun OnDotSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbRadius: Dp = 8.dp,
    trackHeight: Dp = 4.dp,
    activeTrackColor: Color = OnDotColor.Green500,
    inactiveTrackColor: Color = OnDotColor.Gray800,
    thumbColor: Color = OnDotColor.Green500
) {
    // 픽셀 단위로 계산하기 위한 Density
    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { thumbRadius.toPx() }
    val trackHeightPx = with(density) { trackHeight.toPx() }

    // 전체 너비를 측정하기 위한 상태
    var sliderWidth by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .height(max(thumbRadius * 2, trackHeight * 2))
            .onSizeChanged { sliderWidth = it.width.toFloat() }
            // 탭 제스처로 직접 점프
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val new = (offset.x / sliderWidth).coerceIn(0f, 1f)
                    onValueChange(new)
                }
            }
            // 드래그 제스처로 끌어서 변경
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val new = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                    onValueChange(new)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerY = size.height / 2f

            drawRoundRect(
                color = inactiveTrackColor,
                topLeft = Offset(0f, centerY - trackHeightPx / 2),
                size = Size(size.width, trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2)
            )

            drawRoundRect(
                color = activeTrackColor,
                topLeft = Offset(0f, centerY - trackHeightPx / 2),
                size = Size(value * size.width, trackHeightPx),
                cornerRadius = CornerRadius(trackHeightPx / 2)
            )

            drawCircle(
                color = thumbColor,
                center = Offset(value * size.width, centerY),
                radius = thumbRadiusPx
            )
        }
    }
}