package com.dh.ondot.presentation.app

import com.dh.ondot.core.ui.base.UiState
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.response.AlarmDetail
import com.ondot.domain.model.response.Schedule
import com.ondot.domain.model.schedule.SchedulePreparation

data class AppUiState(
    val schedule: Schedule = Schedule(),
    val schedulePreparation: SchedulePreparation = SchedulePreparation(),
    val currentAlarm: AlarmDetail = AlarmDetail(),
    val showPreparationStartAnimation: Boolean = false,
    val showPreparationSnoozeAnimation: Boolean = false,
    val showDepartureSnoozeAnimation: Boolean = false,
    val mapProvider: MapProvider = MapProvider.KAKAO
): UiState
