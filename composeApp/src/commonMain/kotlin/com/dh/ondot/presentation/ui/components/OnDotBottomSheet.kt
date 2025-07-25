package com.dh.ondot.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900

@Composable
fun OnDotBottomSheet(
    content: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900.copy(alpha = 0.8f))
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = {} // 내부 클릭 이벤트 소비
                )
        ) {
            val maxSheetHeight = maxHeight * 0.6f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight)
                    .verticalScroll(scrollState)
                    .background(Gray700, RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp))
                    .imePadding()
                    .padding(horizontal = 22.dp)
                    .padding(top = 32.dp, bottom = 50.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                content()
            }
        }
    }
}