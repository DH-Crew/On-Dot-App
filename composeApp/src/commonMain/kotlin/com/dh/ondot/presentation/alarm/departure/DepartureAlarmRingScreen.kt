package com.dh.ondot.presentation.alarm.departure

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.ondot.domain.model.enums.ButtonType
import com.ondot.domain.model.enums.OnDotTextStyle
import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.util.DateTimeFormatter
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DepartureAlarmRingScreen(
    scheduleId: Long,
    alarmId: Long,
    navigateToSplash: () -> Unit
) {
    val viewModel: AppViewModel = koinViewModel()
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
            alarm = uiState.currentAlarm,
            schedule = uiState.schedule,
            schedulePreparation = uiState.schedulePreparation,
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
    alarm: Alarm,
    schedule: Schedule,
    schedulePreparation: SchedulePreparation,
    showDepartureSnoozeAnimation: Boolean,
    onSnoozeDepartureAlarm: () -> Unit,
    onShowRouteInfo: () -> Unit
) {
    val appointmentDate = DateTimeFormatter.formatKoreanDate(schedule.appointmentAt)
    val appointmentTime = DateTimeFormatter.formatHourMinute(schedule.appointmentAt)
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
                .padding(bottom = if (getPlatform().name == ANDROID) 16.dp else 37.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                    snoozeInterval = alarm.snoozeInterval,
                    appointmentDate = appointmentDate,
                    appointmentTime = appointmentTime,
                    scheduleTitle = schedule.scheduleTitle,
                    schedulePreparation = schedulePreparation,
                    onClickSnooze = onSnoozeDepartureAlarm
                )
            }

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

                AlarmSnoozedSection(
                    schedule = schedule,
                    snoozeInterval = alarm.snoozeInterval,
                    onShowRouteInfo = onShowRouteInfo
                )
            }
        }
    }
}

@Composable
private fun AlarmSnoozedSection(
    schedule: Schedule,
    snoozeInterval: Int,
    onShowRouteInfo: () -> Unit
) {
    val remainingTime = DateTimeFormatter.diffBetweenIsoTimes(schedule.departureAlarm.triggeredAt, schedule.appointmentAt)
    val formattedTime = "${remainingTime.second.toString().padStart(2, '0')}:${remainingTime.third.toString().padStart(2, '0')}"
    val alarmRingTitle = departureSnoozedTitle(formattedTime)
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
            text = alarmRingTitle,
            textColor = Gray0,
            highlight = formattedTime,
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