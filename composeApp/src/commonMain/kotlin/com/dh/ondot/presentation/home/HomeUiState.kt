package com.dh.ondot.presentation.home

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.getPlatform
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.schedule.Schedule
import com.ondot.util.DateTimeFormatter

data class HomeUiState(
    val remainingTime: Triple<Int, Int, Int> = Triple(-1, -1, -1),
    val isExpanded: Boolean = false,
    val scheduleList: List<Schedule> = emptyList(),
    val needsChooseProvider: Boolean = false,
    val mapProviders: List<MapProvider> =
        if (getPlatform().name == ANDROID) listOf(
            MapProvider.KAKAO,
            MapProvider.NAVER,
        )
        else  listOf(
            MapProvider.KAKAO,
            MapProvider.NAVER,
            MapProvider.APPLE
        )
): UiState {
    companion object {
        fun appointmentAtTime(date: String) =
            DateTimeFormatter.formatHourMinute(date)
    }
}
