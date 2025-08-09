package com.dh.ondot.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.presentation.home.components.AddScheduleButton
import com.dh.ondot.presentation.home.components.EmptyScheduleContent
import com.dh.ondot.presentation.home.components.RemainingTimeText
import com.dh.ondot.presentation.home.components.ScheduleList
import com.dh.ondot.presentation.home.components.UserBadgeBanner
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.CREATE_SCHEDULE_GUIDE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import ondot.composeapp.generated.resources.Res
import ondot.composeapp.generated.resources.ic_banner
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
    navigateToGeneralSchedule: () -> Unit,
    navigateToEditSchedule: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.getScheduleList()
    }

    HomeContent(
        uiState = uiState,
        interactionSource = interactionSource,
        onToggle = { viewModel.onToggle() },
        onClickAlarmSwitch = { id, isEnabled -> viewModel.onClickAlarmSwitch(id, isEnabled) },
        navigateToGeneralSchedule = navigateToGeneralSchedule,
        navigateToEditSchedule = navigateToEditSchedule
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    interactionSource: MutableInteractionSource,
    onToggle: () -> Unit,
    onClickAlarmSwitch: (Long, Boolean) -> Unit,
    navigateToGeneralSchedule: () -> Unit,
    navigateToEditSchedule: (Long) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray900)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(33.dp))

            UserBadgeBanner()

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.remainingTime.first != -1) {
                RemainingTimeText(
                    day = uiState.remainingTime.first,
                    hour = uiState.remainingTime.second,
                    minute = uiState.remainingTime.third
                )
            } else {
                OnDotText(text = CREATE_SCHEDULE_GUIDE, style = OnDotTextStyle.TitleMediumSB, color = Gray0)
            }

            Spacer(modifier = Modifier.height(36.dp))

            Image(
                painter = painterResource(Res.drawable.ic_banner),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(349f/100f)
            )

            if (uiState.scheduleList.isEmpty()) {
                Spacer(modifier = Modifier.height(120.dp))

                EmptyScheduleContent()
            } else {
                Spacer(modifier = Modifier.height(24.dp))

                ScheduleList(
                    scheduleList = uiState.scheduleList,
                    interactionSource = interactionSource,
                    onClickSwitch = onClickAlarmSwitch,
                    onClickSchedule = navigateToEditSchedule
                )
            }
        }

        AddScheduleButton(
            isExpanded = uiState.isExpanded,
            interactionSource = interactionSource,
            onToggle = onToggle,
            onQuickAdd = { /*TODO*/ },
            onGeneralAdd = navigateToGeneralSchedule
        )
    }
}