package com.dh.ondot.presentation.alarm.departure

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.alarm.preparation.ScheduleInfoSection
import com.dh.ondot.presentation.app.AppEvent
import com.dh.ondot.presentation.app.AppViewModel
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotHighlightText
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DEPARTURE_ALARM_RING_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.SHOW_ROUTE_INFORMATION_BUTTON_TEXT
import com.dh.ondot.presentation.ui.theme.departureSnoozedTitle
import com.dh.ondot.presentation.ui.theme.formatRemainingSnoozeTime
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res

@Composable
fun DepartureAlarmRingScreen(
    scheduleId: Long,
    alarmId: Long,
    navigateToSplash: () -> Unit
) {
    val viewModel: AppViewModel = viewModel { AppViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(alarmId) {
        if (alarmId != -1L) {
            viewModel.getAlarmInfo(scheduleId, alarmId)
        }
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect {
            when(it) {
                is AppEvent.NavigateToSplash -> navigateToSplash()
            }
        }
    }

    if (uiState.schedule.appointmentAt.isNotBlank()) {
        DepartureAlarmRingContent(
            alarmDetail = uiState.currentAlarm,
            appointmentAt = uiState.schedule.appointmentAt,
            scheduleTitle = uiState.schedule.scheduleTitle,
            showDepartureSnoozeAnimation = uiState.showDepartureSnoozeAnimation,
            onSnoozeDepartureAlarm = viewModel::snoozeDepartureAlarm,
            onShowRouteInfo = viewModel::startDeparture
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Gray900))
    }
}

@Composable
fun DepartureAlarmRingContent(
    alarmDetail: AlarmDetail,
    appointmentAt: String,
    scheduleTitle: String,
    showDepartureSnoozeAnimation: Boolean,
    onSnoozeDepartureAlarm: () -> Unit,
    onShowRouteInfo: () -> Unit
) {
    val appointmentDate = DateTimeFormatter.formatKoreanDate(appointmentAt)
    val appointmentTime = DateTimeFormatter.formatHourMinute(appointmentAt)
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/departure_alarm_snoozed.json").decodeToString())
    }
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = Compottie.IterateForever
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray900)
                .padding(horizontal = 22.dp)
                .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(69.dp))

            OnDotText(
                text = DEPARTURE_ALARM_RING_TITLE,
                style = OnDotTextStyle.TitleMediumSB,
                color = Gray0,
                textAlign = TextAlign.Center
            )

            ScheduleInfoSection(
                snoozeInterval = alarmDetail.snoozeInterval,
                appointmentDate = appointmentDate,
                appointmentTime = appointmentTime,
                scheduleTitle = scheduleTitle,
                onClickSnooze = onSnoozeDepartureAlarm
            )

            Spacer(modifier = Modifier.weight(1f))

            OnDotButton(
                buttonText = SHOW_ROUTE_INFORMATION_BUTTON_TEXT,
                buttonType = ButtonType.Gradient,
                onClick = onShowRouteInfo
            )
        }

        if (showDepartureSnoozeAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray900),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = composition,
                        progress = { progress }
                    ),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                )

                AlarmSnoozedSection(alarmDetail.snoozeInterval, onShowRouteInfo = onShowRouteInfo)
            }
        }
    }
}

@Composable
private fun AlarmSnoozedSection(
    snoozeInterval: Int,
    onShowRouteInfo: () -> Unit
) {
    val totalSeconds = snoozeInterval * 60
    val timeLeftSecond by produceState(initialValue = totalSeconds) {
        var current = totalSeconds

        while (current > 0) {
            delay(1000)
            current--
            value = current
        }
    }
    val minutes = timeLeftSecond / 60
    val seconds = timeLeftSecond % 60
    val timeText = formatRemainingSnoozeTime(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(69.dp))

        OnDotHighlightText(
            text = departureSnoozedTitle("NN:NN"),
            textColor = Gray0,
            highlight = "NN:NN",
            highlightColor = Green500,
            style = OnDotTextStyle.TitleMediumSB,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(79.dp))

        OnDotText(
            text = timeText,
            style = OnDotTextStyle.TitleLargeM,
            color = Red
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = SHOW_ROUTE_INFORMATION_BUTTON_TEXT,
            buttonType = ButtonType.Gradient,
            onClick = onShowRouteInfo
        )

        Spacer(modifier = if (getPlatform().name == ANDROID) Modifier.height(16.dp) else Modifier.height(37.dp))
    }
}