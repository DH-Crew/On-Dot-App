package com.ondot.edit.bottomSheet

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.WORD_COMPETE
import com.ondot.design_system.components.Calendar
import com.ondot.design_system.components.DateSectionHeader
import com.ondot.design_system.components.OnDotBottomSheet
import com.ondot.design_system.components.OnDotButton
import com.ondot.design_system.components.RepeatSettingSection
import com.ondot.domain.model.enums.ButtonType
import com.ondot.util.AnalyticsLogger
import kotlinx.datetime.LocalDate

@Composable
fun EditDateBottomSheet(
    isRepeat: Boolean,
    repeatDays: Set<Int>,
    currentDate: LocalDate,
    onEditDate: (Boolean, Set<Int>, LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val viewModel: EditBottomSheetViewModel = viewModel { EditBottomSheetViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.initDate(isRepeat, repeatDays, currentDate)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clear() }
    }

    OnDotBottomSheet(
        onDismiss = onDismiss,
        contentPaddingTop = 16.dp,
        contentPaddingBottom = 16.dp,
        sheetMaxHeightFraction = 0.8f,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RepeatSettingSection(
                    isRepeat = uiState.isRepeat,
                    activeCheckChip = uiState.activeCheckChip,
                    activeWeekDays = uiState.repeatDays,
                    onClickSwitch = viewModel::onClickSwitch,
                    onClickTextChip = viewModel::onClickTextChip,
                    onClickCheckTextChip = viewModel::onClickCheckTextChip
                )

                HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                DateSectionHeader(
                    selectedDate = uiState.currentDate,
                    interactionSource = interactionSource,
                    isActiveCalendar = true,
                    isRepeat = uiState.isRepeat,
                    activeWeekDays = uiState.repeatDays,
                    onToggleCalendar = {  }
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.fillMaxWidth())

                Calendar(
                    month = uiState.calendarMonth,
                    selectedDate = uiState.currentDate,
                    isRepeat = uiState.isRepeat,
                    activeWeekDays = uiState.repeatDays,
                    onPrevMonth = viewModel::onPrevMonth,
                    onNextMonth = viewModel::onNextMonth,
                    onDateSelected = viewModel::onDateSelected
                )

                Spacer(modifier = Modifier.height(26.dp))

                Spacer(modifier = Modifier.weight(1f))

                OnDotButton(
                    buttonText = WORD_COMPETE,
                    buttonType = ButtonType.Green500,
                    onClick = {
                        onEditDate(uiState.isRepeat, uiState.repeatDays, uiState.currentDate)
                        onDismiss()
                    }
                )
            }
        }
    )
}