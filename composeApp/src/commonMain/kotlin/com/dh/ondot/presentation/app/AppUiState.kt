package com.dh.ondot.presentation.app

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.response.AlarmDetail
import com.dh.ondot.domain.model.response.Schedule

data class AppUiState(
    val schedule: Schedule = Schedule(),
    val currentAlarm: AlarmDetail = AlarmDetail(),
    val showPreparationStartAnimation: Boolean = false,
    val showPreparationSnoozeAnimation: Boolean = false,
    val showDepartureSnoozeAnimation: Boolean = false,
    val mapProvider: MapProvider = MapProvider.KAKAO
): UiState
