package com.dh.ondot.presentation.edit.bottomSheet

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.presentation.general.repeat.components.TimePicker
import com.dh.ondot.presentation.general.repeat.components.TimeSectionHeader
import com.dh.ondot.presentation.ui.components.OnDotBottomSheet
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.WORD_COMPETE
import kotlinx.datetime.LocalTime

@Composable
fun EditTimeBottomSheet(
    currentTime: LocalTime,
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
) {
    val viewModel: EditBottomSheetViewModel = viewModel { EditBottomSheetViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    val periodState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    LaunchedEffect(Unit) {
        viewModel.initTime(currentTime)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    OnDotBottomSheet(
        onDismiss = onDismiss,
        contentPaddingTop = 16.dp,
        contentPaddingBottom = 16.dp,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                TimeSectionHeader(
                    selectedTime = uiState.currentTime,
                    isActiveDial = true,
                    interactionSource = interactionSource,
                    onToggleDial = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(modifier = Modifier.padding(horizontal = 4.dp), thickness = (0.5).dp, color = Gray600)

                TimePicker(
                    periodState = periodState,
                    hourState = hourState,
                    minuteState = minuteState,
                    onTimeSelected = viewModel::onTimeSelected
                )

                Spacer(modifier = Modifier.height(26.dp))

                OnDotButton(
                    buttonText = WORD_COMPETE,
                    buttonType = ButtonType.Green500,
                    onClick = {
                        onTimeSelected(uiState.currentTime)
                    }
                )
            }
        }
    )
}