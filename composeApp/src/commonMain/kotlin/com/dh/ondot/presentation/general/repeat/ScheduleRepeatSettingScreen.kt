package com.dh.ondot.presentation.general.repeat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dh.ondot.domain.model.enums.ChipStyle
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.enums.RepeatType
import com.dh.ondot.domain.model.enums.TopBarType
import com.dh.ondot.presentation.general.GeneralScheduleUiState
import com.dh.ondot.presentation.general.GeneralScheduleViewModel
import com.dh.ondot.presentation.ui.components.CheckTextChip
import com.dh.ondot.presentation.ui.components.OnDotSwitch
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.components.StepProgressIndicator
import com.dh.ondot.presentation.ui.components.TextChip
import com.dh.ondot.presentation.ui.components.TopBar
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray600
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.SCHEDULE_REPEAT_TITLE
import com.dh.ondot.presentation.ui.theme.WORD_REPEAT

@Composable
fun ScheduleRepeatSettingScreen(
    viewModel: GeneralScheduleViewModel,
    navigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.totalStep) {
        if (uiState.totalStep == 0) {
            viewModel.initStep()
        }
    }

    ScheduleRepeatSettingContent(
        uiState = uiState,
        onClickSwitch = viewModel::onClickSwitch,
        onClickCheckTextChip = viewModel::onClickCheckTextChip,
        onClickTextChip = viewModel::onClickTextChip,
        navigateToMain = navigateToMain
    )
}

@Composable
fun ScheduleRepeatSettingContent(
    uiState: GeneralScheduleUiState,
    onClickSwitch: (Boolean) -> Unit,
    onClickCheckTextChip: (Int) -> Unit,
    onClickTextChip: (Int) -> Unit,
    navigateToMain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
            .padding(horizontal = 22.dp)
    ) {
        TopBar(type = TopBarType.CLOSE, onClick = navigateToMain)

        Spacer(modifier = Modifier.height(24.dp))

        StepProgressIndicator(totalStep = uiState.totalStep, currentStep = uiState.currentStep)

        Spacer(modifier = Modifier.height(34.dp))

        OnDotText(
            text = SCHEDULE_REPEAT_TITLE,
            style = OnDotTextStyle.TitleMediumM,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(48.dp))

        RepeatSettingSection(
            isRepeat = uiState.isRepeat,
            activeCheckChip = uiState.activeCheckChip,
            activeWeekDays = uiState.activeWeekDays,
            onClickSwitch = onClickSwitch,
            onClickCheckTextChip = onClickCheckTextChip,
            onClickTextChip = onClickTextChip
        )
    }
}

@Composable
fun RepeatSettingSection(
    isRepeat: Boolean,
    activeCheckChip: Int?,
    activeWeekDays: Set<Int>,
    onClickSwitch: (Boolean) -> Unit,
    onClickCheckTextChip: (Int) -> Unit,
    onClickTextChip: (Int) -> Unit
) {
    val weekDays = arrayOf("일", "월", "화", "수", "목", "금", "토")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnDotText(
                text = WORD_REPEAT,
                style = OnDotTextStyle.BodyLargeR1,
                color = Gray0
            )

            Spacer(modifier = Modifier.weight(1f))

            OnDotSwitch(
                checked = isRepeat,
                onClick = onClickSwitch
            )
        }

        if (isRepeat) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(thickness = (0.5).dp, color = Gray600, modifier = Modifier.padding(horizontal = 4.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (type in RepeatType.entries) {
                    CheckTextChip(
                        text = type.title,
                        chipStyle = if (activeCheckChip == null && activeWeekDays.isEmpty()) ChipStyle.Normal
                        else {
                            if (activeCheckChip == type.index) ChipStyle.Active
                            else ChipStyle.Inactive
                        },
                        onClick = { onClickCheckTextChip(type.index) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0..6) {
                    TextChip(
                        text = weekDays[i],
                        chipStyle = if (activeWeekDays.isEmpty()) ChipStyle.Normal
                        else {
                            if (activeWeekDays.contains(i)) ChipStyle.Active
                            else ChipStyle.Inactive
                        },
                        onClick = { onClickTextChip(i) }
                    )
                }
            }
        }
    }
}