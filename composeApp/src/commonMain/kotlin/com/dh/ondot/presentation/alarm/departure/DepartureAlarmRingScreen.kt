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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dh.ondot.core.util.DateTimeFormatter
import com.dh.ondot.domain.model.enums.ButtonType
import com.dh.ondot.domain.model.enums.OnDotTextStyle
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.alarm.preparation.AlarmSnoozedSection
import com.dh.ondot.presentation.alarm.preparation.ScheduleInfoSection
import com.dh.ondot.presentation.app.AppViewModel
import com.dh.ondot.presentation.ui.components.OnDotButton
import com.dh.ondot.presentation.ui.components.OnDotText
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.dh.ondot.presentation.ui.theme.DEPARTURE_ALARM_RING_TITLE
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray0
import com.dh.ondot.presentation.ui.theme.OnDotColor.Gray900
import com.dh.ondot.presentation.ui.theme.PREPARATION_START_BUTTON_TEXT
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import ondot.composeapp.generated.resources.Res

@Composable
fun DepartureAlarmRingScreen(
    alarmId: Long,
    navigateToSplash: () -> Unit
) {
    val viewModel: AppViewModel = viewModel { AppViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(alarmId) {
        if (alarmId != -1L) {
            viewModel.getAlarmInfo(alarmId)
        }
    }

    LaunchedEffect(uiState.showDepartureSnoozeAnimation) {
        if (uiState.showDepartureSnoozeAnimation) {
            delay(2000L)
            navigateToSplash()
        }
    }

    DepartureAlarmRingContent(
        alarmDetail = uiState.alarmRingInfo.alarmDetail,
        appointmentAt = uiState.alarmRingInfo.appointmentAt,
        scheduleTitle = uiState.alarmRingInfo.scheduleTitle,
        showDepartureSnoozeAnimation = uiState.showDepartureSnoozeAnimation,
        onSnoozeDepartureAlarm = viewModel::snoozeDepartureAlarm
    )
}

@Composable
fun DepartureAlarmRingContent(
    alarmDetail: AlarmDetail,
    appointmentAt: String,
    scheduleTitle: String,
    showDepartureSnoozeAnimation: Boolean,
    onSnoozeDepartureAlarm: () -> Unit
) {
    val appointmentDate = DateTimeFormatter.formatKoreanDate(appointmentAt)
    val appointmentTime = DateTimeFormatter.formatHourMinute(appointmentAt)
    var isSnoozed by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/lotties/preparation_alarm_snoozed.json").decodeToString())
    }
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = Compottie.IterateForever
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showDepartureSnoozeAnimation) {
            Box(
                modifier = Modifier.fillMaxSize().background(Gray900)
            ) {
                Image(
                    painter = rememberLottiePainter(
                        composition = composition,
                        progress = { progress }
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

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
                color = Gray0
            )

            if (isSnoozed) {
                AlarmSnoozedSection(alarmDetail.snoozeInterval)
            } else {
                ScheduleInfoSection(
                    snoozeInterval = alarmDetail.snoozeInterval,
                    appointmentDate = appointmentDate,
                    appointmentTime = appointmentTime,
                    scheduleTitle = scheduleTitle,
                    onClickSnooze = { isSnoozed = true }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            OnDotButton(
                buttonText = PREPARATION_START_BUTTON_TEXT,
                buttonType = ButtonType.Gradient,
                onClick = onSnoozeDepartureAlarm
            )
        }
    }
}