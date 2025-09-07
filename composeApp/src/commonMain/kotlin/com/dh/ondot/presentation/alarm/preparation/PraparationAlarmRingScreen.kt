package com.dh.ondot.presentation.alarm.preparation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.domain.model.response.Schedule
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.app.AppViewModel
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotHighlightText
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray200
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray700
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.OnDotColor.Green500
import com.dh.ondot.presentation.ui.theme.OnDotColor.Red
import com.dh.ondot.presentation.ui.theme.PREPARATION_START_BUTTON_TEXT
import com.dh.ondot.presentation.ui.theme.alarmRingTitle
import com.dh.ondot.presentation.ui.theme.formatRemainingSnoozeTime
import com.dh.ondot.presentation.ui.theme.snoozeIntervalLabel
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res

@Composable
fun PreparationAlarmRingScreen(
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

    LaunchedEffect(uiState.showPreparationStartAnimation) {
        if (uiState.showPreparationStartAnimation) {
            delay(2000L)
            navigateToSplash()
        }
    }

    LaunchedEffect(Unit) {
        if (uiState.showPreparationSnoozeAnimation || uiState.showDepartureSnoozeAnimation) {
            viewModel.initAnimationFlags()
        }
    }

    if (uiState.schedule.appointmentAt.isNotBlank()) {
        PreparationAlarmRingContent(
            alarmDetail = uiState.currentAlarm,
            schedule = uiState.schedule,
            showPreparationStartAnimation = uiState.showPreparationStartAnimation,
            showPreparationSnoozeAnimation = uiState.showPreparationSnoozeAnimation,
            onClickPreparationStartButton = { viewModel.startPreparation() },
            onSnoozePreparationAlarm = viewModel::snoozePreparationAlarm
        )
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Gray900))
    }
}

@Composable
fun PreparationAlarmRingContent(
    alarmDetail: AlarmDetail,
    schedule: Schedule,
    showPreparationStartAnimation: Boolean,
    showPreparationSnoozeAnimation: Boolean,
    onClickPreparationStartButton: () -> Unit,
    onSnoozePreparationAlarm: () -> Unit
) {
    val remainingTime = DateTimeFormatter.diffBetweenIsoTimes(schedule.preparationAlarm.triggeredAt, schedule.departureAlarm.triggeredAt)
    val formattedTime = "${remainingTime.second.toString().padStart(2, '0')}:${remainingTime.third.toString().padStart(2, '0')}"
    val alarmRingTitle = alarmRingTitle(formattedTime)
    val appointmentDate = DateTimeFormatter.formatKoreanDate(schedule.appointmentAt)
    val appointmentTime = DateTimeFormatter.formatHourMinute(schedule.appointmentAt)
    val startComposition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/preparation_start.json").decodeToString())
    }
    val startProgress by animateLottieCompositionAsState(
        startComposition,
        iterations = Compottie.IterateForever
    )
    val snoozedComposition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/preparation_alarm_snoozed.json").decodeToString())
    }
    val snoozedProgress by animateLottieCompositionAsState(
        snoozedComposition,
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

            AlarmRingTitle(alarmRingTitle, formattedTime)

            ScheduleInfoSection(
                snoozeInterval = alarmDetail.snoozeInterval,
                appointmentDate = appointmentDate,
                appointmentTime = appointmentTime,
                scheduleTitle = schedule.scheduleTitle,
                onClickSnooze = onSnoozePreparationAlarm
            )

            Spacer(modifier = Modifier.weight(1f))

            OnDotButton(
                buttonText = PREPARATION_START_BUTTON_TEXT,
                buttonType = ButtonType.Green500,
                onClick = onClickPreparationStartButton
            )
        }

        if (showPreparationStartAnimation) {
            Box(
                modifier = Modifier.fillMaxSize().background(Gray900)
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = startComposition,
                        progress = { startProgress }
                    ),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                )
            }
        }

        if (showPreparationSnoozeAnimation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray900),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = snoozedComposition,
                        progress = { snoozedProgress }
                    ),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                )

                AlarmSnoozedSection(
                    alarmRingTitle = alarmRingTitle,
                    formattedText = formattedTime,
                    snoozeInterval = alarmDetail.snoozeInterval,
                    onStartPreparation = onClickPreparationStartButton
                )
            }
        }
    }
}

@Composable
fun AlarmRingTitle(
    title: String,
    highlight: String,
) {
    OnDotHighlightText(
        text = title,
        textColor = Gray0,
        highlight = highlight,
        highlightColor = Green500,
        style = OnDotTextStyle.TitleMediumSB,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SnoozeButton(
    snoozeInterval: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Gray700, RoundedCornerShape(12.dp))
            .padding(vertical = 18.dp, horizontal = 28.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        OnDotText(text = snoozeIntervalLabel(snoozeInterval), style = OnDotTextStyle.TitleSmallSB, color = Gray200)
    }
}

@Composable
fun ScheduleInfoSection(
    snoozeInterval: Int,
    appointmentDate: String,
    appointmentTime: String,
    scheduleTitle: String,
    onClickSnooze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(54.dp))

        OnDotText(
            text = appointmentDate,
            style = OnDotTextStyle.TitleSmallM,
            color = Gray0
        )

        OnDotText(
            text = appointmentTime,
            style = OnDotTextStyle.TitleLargeM,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(16.dp))

        OnDotText(
            text = scheduleTitle,
            style = OnDotTextStyle.BodyLargeR1,
            color = Gray0
        )

        Spacer(modifier = Modifier.height(213.dp))

        SnoozeButton(
            snoozeInterval = snoozeInterval,
            onClick = onClickSnooze
        )
    }
}

@Composable
private fun AlarmSnoozedSection(
    alarmRingTitle: String,
    formattedText: String,
    snoozeInterval: Int,
    onStartPreparation: () -> Unit
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

        AlarmRingTitle(alarmRingTitle, formattedText)

        Spacer(modifier = Modifier.height(79.dp))

        OnDotText(
            text = timeText,
            style = OnDotTextStyle.TitleLargeM,
            color = Red
        )

        Spacer(modifier = Modifier.weight(1f))

        OnDotButton(
            buttonText = PREPARATION_START_BUTTON_TEXT,
            buttonType = ButtonType.Green500,
            onClick = onStartPreparation
        )

        Spacer(modifier = if (getPlatform().name == ANDROID) Modifier.height(16.dp) else Modifier.height(37.dp))
    }
}
