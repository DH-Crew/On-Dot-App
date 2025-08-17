package com.dh.ondot.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.WORD_DELETE
import kotlinx.coroutines.launch
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_delete_gray800
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipableDeleteItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    deleteWidth: Dp = 80.dp,
    thresholdFraction: Float = 0.3f,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val maxRevealPx = with(density) { deleteWidth.toPx() }
    val offsetX = remember { Animatable(0f) }

    fun snapTo(target: Float) = scope.launch {
        offsetX.animateTo(
            target,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .matchParentSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(deleteWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(8.dp)
                    .semantics(mergeDescendants = true) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_delete_gray800),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Gray0),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(7.dp))
                    OnDotText(text = WORD_DELETE, style = OnDotTextStyle.BodyMediumR, color = Gray0)
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { // Animatable px -> IntOffset
                    androidx.compose.ui.unit.IntOffset(offsetX.value.roundToInt(), 0)
                }
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val next = (offsetX.value + dragAmount).coerceIn(-maxRevealPx, 0f)
                            scope.launch { offsetX.snapTo(next) }
                        },
                        onDragEnd = {
                            val shouldOpen = abs(offsetX.value) > maxRevealPx * thresholdFraction
                            snapTo(if (shouldOpen) -maxRevealPx else 0f)
                        },
                        onDragCancel = {
                            snapTo(0f)
                        }
                    )
                }
        ) {
            content()

            // 오른쪽 노출 영역 위로 클릭 오버레이(완전히 열렸을 때만 삭제 트리거)
            if (offsetX.value <= -maxRevealPx + 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(deleteWidth)
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, _ -> /* open 상태 유지 */ }
                        }
                        .padding(0.dp)
                        .background(Color.Transparent),
                    content = {}
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(deleteWidth)
                .align(Alignment.CenterEnd)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, _ -> /* swallow */ }
                }
                .padding(0.dp)
                .background(Color.Transparent)
        ) {
            // 터치 처리: 충분히 열려 있으면 삭제
            Box(
                Modifier
                    .matchParentSize()
                    .pointerInput(offsetX.value) {
                        // 열린 상태에서 탭하면 삭제
                        detectTapGestures(
                            onTap = {
                                if (offsetX.value <= -maxRevealPx * 0.95f) {
                                    onDelete()
                                    snapTo(0f)
                                } else {
                                    // 충분히 안 열려 있으면 열어두기
                                    snapTo(-maxRevealPx)
                                }
                            }
                        )
                    }
            )
        }
    }
}