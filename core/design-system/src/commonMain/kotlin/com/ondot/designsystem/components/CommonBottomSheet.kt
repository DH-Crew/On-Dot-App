package com.ondot.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ondot.designsystem.theme.OnDotColor.Gray700
import com.ondot.designsystem.theme.OnDotColor.Gray900

private const val BOTTOM_SHEET_ANIMATION_DURATION_MILLIS = 180

@Composable
fun CommonBottomSheet(
    visibleState: MutableTransitionState<Boolean>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingTop: Dp = 32.dp,
    contentPaddingBottom: Dp = 50.dp,
    sheetMaxHeightFraction: Float = 0.6f,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visibleState.currentState && !visibleState.targetState) return

    val scrimInteractionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var sheetHeightPx by remember { mutableStateOf(0) }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .background(Gray900.copy(alpha = 0.8f)),
    ) {
        val sheetHeight = with(density) { sheetHeightPx.toDp() }
        val scrimHeight = (maxHeight - sheetHeight).coerceAtLeast(0.dp)

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(scrimHeight)
                    .clickable(
                        indication = null,
                        interactionSource = scrimInteractionSource,
                        onClick = onDismiss,
                    ),
        )

        AnimatedVisibility(
            visibleState = visibleState,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            enter =
                slideInVertically(
                    animationSpec = tween(durationMillis = BOTTOM_SHEET_ANIMATION_DURATION_MILLIS),
                ) { fullHeight ->
                    fullHeight
                } + fadeIn(animationSpec = tween(durationMillis = BOTTOM_SHEET_ANIMATION_DURATION_MILLIS)),
            exit =
                slideOutVertically(
                    animationSpec = tween(durationMillis = BOTTOM_SHEET_ANIMATION_DURATION_MILLIS),
                ) { fullHeight ->
                    fullHeight
                } + fadeOut(animationSpec = tween(durationMillis = BOTTOM_SHEET_ANIMATION_DURATION_MILLIS)),
        ) {
            BoxWithConstraints(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            sheetHeightPx = coordinates.size.height
                        },
            ) {
                val maxSheetHeight = maxHeight * sheetMaxHeightFraction

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = maxSheetHeight)
                            .verticalScroll(state = scrollState, enabled = scrollable)
                            .background(Gray700, RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                            .imePadding()
                            .padding(horizontal = 22.dp)
                            .padding(top = contentPaddingTop, bottom = contentPaddingBottom),
                    verticalArrangement = Arrangement.Bottom,
                    content = content,
                )
            }
        }
    }
}
