package com.ondot.edit

import com.ondot.domain.model.enums.TimeBottomSheet
import com.ondot.domain.model.enums.TimeType
import com.ondot.domain.model.schedule.ScheduleDetail
import com.ondot.ui.base.UiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

data class EditScheduleUiState @OptIn(ExperimentalTime::class) constructor(
    val scheduleId: Long = -1,
    val isInitialized: Boolean = false,
    val schedule: ScheduleDetail = ScheduleDetail(),
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime =
        kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .time,
    val selectedAlarmDate: LocalDate? = null,
    val showDeleteDialog: Boolean = false,
    val showDateBottomSheet: Boolean = false,
    val activeTimeBottomSheet: TimeBottomSheet? = null,
    val selectedTimeType: TimeType = TimeType.DEPARTURE, // 시간을 수정하는 바텀시트가 어떤 것에 의해 렌더링되었는지 저장
) : UiState
