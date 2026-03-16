package com.ondot.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.WORD_RESTORE_ACTION
import com.ondot.designsystem.components.OnDotText
import com.ondot.designsystem.theme.OnDotColor.Gray0
import com.ondot.designsystem.theme.OnDotColor.Gray200
import com.ondot.designsystem.theme.OnDotColor.Gray800
import com.ondot.designsystem.theme.OnDotColor.Green900
import com.ondot.designsystem.theme.OnDotColor.Red
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.ui.ToastData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_circle_check_green
import ondot.core.design_system.generated.resources.ic_circle_check_red
import ondot.core.design_system.generated.resources.ic_circle_error_red
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val toasts = ToastManager.toasts
    var current by remember { mutableStateOf<ToastData?>(null) }
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var dismissJob by remember { mutableStateOf<Job?>(null) }
    val animationMs = 200

    LaunchedEffect(ToastManager) {
        toasts.collectLatest { toast ->
            dismissJob?.cancel()
            current = toast
            visible = true

            dismissJob =
                scope.launch {
                    delay(toast.duration)
                    visible = false
                    delay(animationMs.toLong())
                    if (current == toast) current = null
                }
        }
    }

    fun dismiss() {
        val toastAtDismiss = current ?: return

        dismissJob?.cancel()
        dismissJob =
            scope.launch {
                visible = false
                delay(animationMs.toLong())
                if (current == toastAtDismiss) {
                    current = null
                }
            }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        current?.let { toast ->
            AnimatedVisibility(
                visible = visible && current != null,
                enter =
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(animationMs),
                    ) + fadeIn(tween(animationMs)),
                exit =
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(animationMs),
                    ) + fadeOut(tween(animationMs)),
            ) {
                ToastItem(
                    data = toast,
                    onDismiss = ::dismiss,
                )
            }
        }
    }
}

@Composable
private fun ToastItem(
    data: ToastData,
    onDismiss: () -> Unit,
) {
    val bg = Gray800
    val icon =
        when (data.type) {
            ToastType.INFO -> Res.drawable.ic_circle_check_green
            ToastType.DELETE -> Res.drawable.ic_circle_check_red
            ToastType.ERROR -> Res.drawable.ic_circle_error_red
        }
    val textColor = Gray0
    val strokeColor =
        when (data.type) {
            ToastType.INFO -> Green900
            else -> Red
        }
    val cornerRadius = RoundedCornerShape(99.dp)

    Box(
        modifier =
            Modifier
                .padding(bottom = 90.dp)
                .padding(horizontal = 22.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .background(bg, cornerRadius)
                    .border(1.dp, strokeColor, cornerRadius)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )

            Spacer(Modifier.width(8.dp))

            OnDotText(
                text = data.message,
                color = textColor,
                style = OnDotTextStyle.BodyLargeSB,
                textAlign = TextAlign.Start,
            )

            if (data.type == ToastType.DELETE) {
                Spacer(Modifier.width(20.dp))

                OnDotText(
                    text = WORD_RESTORE_ACTION,
                    style = OnDotTextStyle.BodyMediumR,
                    color = Gray200,
                    modifier =
                        Modifier.clickable {
                            data.callback()
                            onDismiss()
                        },
                )
            }
        }
    }
}
