package com.ondot.ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.rotatingGlowStroke(
    enabled: Boolean,
    cornerRadius: Dp = 12.dp,
    strokeWidth: Dp = 1.0.dp,
    // 기본 테두리 색상: Green500
    baseColor: Color,
    baseAlpha: Float = 0.20f,
    // 하이라이트 선두 색상: Gray700에 alpha 0.4 적용
    highlightStartColor: Color,
    // 하이라이트 꼬리 색상: Green500
    highlightEndColor: Color,
    // 하이라이트 길이
    highlightLengthFraction: Float = 0.65f,
    periodMs: Int = 2400,
    // 두 조각으로 나뉘는 wrap-around 구간에서 cap이 겹쳐 보이면 Butt으로 변경
    cap: StrokeCap = StrokeCap.Butt,
    // 글로우 사용 여부
    glowEnabled: Boolean = true,
    glowWidthMultiplier: Float = 2.2f,
    glowAlpha: Float = 0.28f
): Modifier = composed {
    if (!enabled) return@composed this

    val transition = rememberInfiniteTransition(label = "rotatingGlowStroke")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs, easing = LinearEasing)
        ),
        label = "progress"
    )

    drawWithCache {
        val strokePx = strokeWidth.toPx()
        val radiusPx = cornerRadius.toPx()

        // stroke가 바깥으로 삐져나가지 않게 inset
        val inset = strokePx / 2f

        // border 경로
        val borderPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = inset,
                    top = inset,
                    right = size.width - inset,
                    bottom = size.height - inset,
                    cornerRadius = CornerRadius(radiusPx, radiusPx)
                )
            )
        }

        val borderMeasure = PathMeasure().apply { setPath(borderPath, false) }
        val borderLength = borderMeasure.length.coerceAtLeast(1f)
        val segLen = (borderLength * highlightLengthFraction).coerceIn(1f, borderLength)

        /**
         * segPath(하이라이트 경로 조각)에 대해
         * 진행 방향(시작 -> 끝) 기준으로 c0 -> c1 리니어 그라데이션을 적용
         */
        fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSegmentWithGradient(
            segPath: Path,
            c0: Color,
            c1: Color
        ) {
            val pm = PathMeasure().apply { setPath(segPath, false) }
            val segLength = pm.length
            if (segLength <= 0f) return

            val p0: Offset = pm.getPosition(0f)
            val p1: Offset = pm.getPosition(segLength)

            val brush = Brush.linearGradient(
                colors = listOf(c0, c1),
                start = p0,
                end = p1
            )

            if (glowEnabled) {
                drawPath(
                    path = segPath,
                    brush = brush,
                    style = Stroke(width = strokePx * glowWidthMultiplier, cap = cap),
                    alpha = glowAlpha
                )
            }

            drawPath(
                path = segPath,
                brush = brush,
                style = Stroke(width = strokePx, cap = cap)
            )
        }

        onDrawWithContent {
            drawContent()

            // 기본 테두리
            drawPath(
                path = borderPath,
                color = baseColor.copy(alpha = baseAlpha),
                style = Stroke(width = strokePx, cap = cap)
            )

            // 이동하는 하이라이트 구간
            val start = (progress * borderLength) % borderLength
            val end = start + segLen

            if (end <= borderLength) {
                // 하나의 조각으로 끝나는 케이스
                val segPath = Path()
                borderMeasure.getSegment(start, end, segPath, true)

                drawSegmentWithGradient(
                    segPath = segPath,
                    c0 = highlightStartColor,
                    c1 = highlightEndColor
                )
            } else {
                // wrap-around: 두 조각으로 나눠 그리기
                val segPath1 = Path()
                val segPath2 = Path()

                val len1 = borderLength - start       // 1조각 길이
                val len2 = end - borderLength         // 2조각 길이

                borderMeasure.getSegment(start, borderLength, segPath1, true)
                borderMeasure.getSegment(0f, len2, segPath2, true)

                // 색 끊김(seam) 방지:
                // 전체 하이라이트(c0 -> c1)에서 1조각이 차지하는 비율만큼 진행된 중간색을 만들어 이어줌
                val frac1 = (len1 / segLen).coerceIn(0f, 1f)
                val midColor = lerp(highlightStartColor, highlightEndColor, frac1)

                // 1조각: Green500 -> mid
                drawSegmentWithGradient(segPath1, highlightStartColor, midColor)
                // 2조각: mid -> Gray700(0.4)
                drawSegmentWithGradient(segPath2, midColor, highlightEndColor)
            }
        }
    }
}