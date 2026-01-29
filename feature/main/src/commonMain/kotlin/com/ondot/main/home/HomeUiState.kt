package com.ondot.main.home

import androidx.compose.runtime.Immutable
import com.dh.ondot.presentation.ui.theme.ANDROID
import com.ondot.design_system.getPlatform
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.schedule.Schedule
import com.ondot.ui.base.UiState
import com.ondot.util.DateTimeFormatter

@Immutable
data class HomeUiState(
    val earliestScheduleId: Long = -1L,
    val remainingTime: Triple<Int, Int, Int> = Triple(-1, -1, -1),
    val isExpanded: Boolean = false,
    val scheduleList: List<Schedule> = emptyList(),
    val needsChooseProvider: Boolean = false,
    val mapProviders: List<MapProvider> =
        if (getPlatform() == ANDROID) listOf(
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
