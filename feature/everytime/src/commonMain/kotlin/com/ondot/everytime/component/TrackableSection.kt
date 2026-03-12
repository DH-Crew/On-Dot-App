package com.ondot.everytime.component

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

@Composable
fun TrackableSection(
    id: String,
    order: Int,
    viewportRect: Rect?,
    minVisiblePx: Int,
    visibleMap: MutableMap<String, Boolean>,
    content: @Composable () -> Unit,
) {
    var animateIn by remember { mutableStateOf(false) }

    // 현재 섹션이 충분히 보이는지 계산해서 visibleMap 업데이트
    val trackModifier =
        Modifier.onGloballyPositioned { coords ->
            val vp = viewportRect ?: return@onGloballyPositioned
            val bounds = coords.boundsInWindow()
            val overlap =
                (min(bounds.bottom, vp.bottom) - max(bounds.top, vp.top))
                    .toInt()
                    .coerceAtLeast(0)
            val isVisibleNow = overlap >= minVisiblePx

            // 값이 바뀔 때만 갱신
            if (visibleMap[id] != isVisibleNow) {
                visibleMap[id] = isVisibleNow
            }
        }
    val isVisible = visibleMap[id] == true

    // 뷰포트 진입/이탈에 따라 애니메이션 상태를 토글
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay((80 + order * 60).toLong())
            animateIn = true
        } else {
            // 화면 밖으로 나가면 리셋 → 다시 들어오면 재생됨
            delay((80 + order * 90).toLong())
            animateIn = false
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = EaseOutCubic),
        label = "alpha",
    )
    val translateY by animateDpAsState(
        targetValue = if (animateIn) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 650, easing = EaseOutCubic),
        label = "ty",
    )
    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.97f,
        animationSpec = tween(durationMillis = 650, easing = EaseOutCubic),
        label = "scale",
    )

    Box(
        modifier =
            trackModifier.graphicsLayer {
                this.alpha = alpha
                translationY = translateY.toPx()
                scaleX = scale
                scaleY = scale
            },
    ) {
        content()
    }
}
