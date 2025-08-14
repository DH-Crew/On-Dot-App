package com.dh.ondot.presentation.edit

import com.dh.ondot.core.ui.base.UiState
import com.dh.ondot.domain.model.enums.TimeBottomSheet
import com.dh.ondot.domain.model.enums.TimeType
import com.dh.ondot.domain.model.response.ScheduleDetail
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class EditScheduleUiState(
    val scheduleId: Long = -1,
    val isInitialized: Boolean = false,
    val schedule: ScheduleDetail = ScheduleDetail(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time,
    val selectedAlarmDate: LocalDate? = null,
    val showDeleteDialog: Boolean = false,
    val showDateBottomSheet: Boolean = false,
    val activeTimeBottomSheet: TimeBottomSheet? = null,
    val selectedTimeType: TimeType = TimeType.DEPARTURE, // 시간을 수정하는 바텀시트가 어떤 것에 의해 렌더링되었는지 저장
) : UiState
