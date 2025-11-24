package com.dh.ondot.presentation.app

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.alarm.Alarm
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.ui.base.UiState

data class AlarmUiState(
    val schedule: Schedule = Schedule(),
    val schedulePreparation: SchedulePreparation = SchedulePreparation(),
    val currentAlarm: Alarm = Alarm(),
    val showPreparationStartAnimation: Boolean = false,
    val showPreparationSnoozeAnimation: Boolean = false,
    val showDepartureSnoozeAnimation: Boolean = false,
    val mapProvider: MapProvider = MapProvider.KAKAO
): UiState
