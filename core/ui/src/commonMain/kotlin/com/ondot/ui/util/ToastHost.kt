package com.ondot.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.WORD_RESTORE_ACTION
import com.ondot.design_system.components.OnDotText
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.ui.ToastData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import ondot.core.design_system.generated.resources.Res
import ondot.core.design_system.generated.resources.ic_circle_check_green
import ondot.core.design_system.generated.resources.ic_circle_check_red
import ondot.core.design_system.generated.resources.ic_circle_error_red
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToastHost(
    modifier: Modifier = Modifier
) {
    val toasts = ToastManager.toasts
    var current by remember { mutableStateOf<ToastData?>(null) }

    LaunchedEffect(Unit) {
        toasts
            .collectLatest { toast ->
                current = toast
                delay(toast.duration)
                current = null
            }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        current?.let { toast ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                ToastItem(
                    data = toast,
                    onDismiss = { current = null }
                )
            }
        }
    }
}

@Composable
private fun ToastItem(
    data: ToastData,
    onDismiss: () -> Unit
) {
    val bg = OnDotColor.Gray500
    val icon = when(data.type) {
        ToastType.INFO    -> Res.drawable.ic_circle_check_green
        ToastType.DELETE -> Res.drawable.ic_circle_check_red
        ToastType.ERROR   -> Res.drawable.ic_circle_error_red
    }
    val textColor = when(data.type) {
        ToastType.INFO    -> OnDotColor.Green700
        ToastType.DELETE, ToastType.ERROR -> OnDotColor.Red
    }

    Box(modifier = Modifier
        .padding(bottom = 90.dp)
        .padding(horizontal = 22.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(bg, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { onDismiss() }
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(8.dp))

            OnDotText(
                text = data.message,
                color = textColor,
                style = OnDotTextStyle.BodyLargeSB,
                modifier = Modifier.weight(1f)
            )

            if (data.type == ToastType.DELETE) {
                OnDotText(
                    text = WORD_RESTORE_ACTION,
                    style = OnDotTextStyle.BodyMediumR,
                    color = Gray200,
                    modifier = Modifier.clickable { data.callback(); onDismiss() }
                )
            }
        }
    }
}