package com.ondot.everytime.contract

import androidx.compose.runtime.Immutable
import com.ondot.domain.model.enums.DayOfWeekKey
import com.ondot.domain.model.schedule.EverytimeValidateTimetable
import com.ondot.ui.base.UiState
import com.ondot.ui.screen.placepicker.model.PlacePickerUiModel

@Immutable
data class EverytimeUiState(
    val timetable: EverytimeValidateTimetable? = null,
    val selectedClassIdByDay: Map<DayOfWeekKey, String> = emptyMap(),
    val placePickerState: PlacePickerUiModel = PlacePickerUiModel(),
) : UiState {
    val classes: List<TimetableClassUiModel>
        get() {
            val source = timetable ?: return emptyList()

            return source.timetable
                .flatMap { (day, entries) ->
                    entries.map { entry ->
                        val id = "${day.name}_${entry.courseName}_${entry.startTime}_${entry.endTime}"
                        TimetableClassUiModel(
                            id = id,
                            day = day,
                            courseName = entry.courseName,
                            startTime = entry.startTime,
                            endTime = entry.endTime,
                            isSelected = selectedClassIdByDay[day] == id,
                        )
                    }
                }.sortedWith(
                    compareBy(
                        { it.day.order },
                        { it.startTime.hour },
                        { it.startTime.minute },
                    ),
                )
        }

    val canGoNext: Boolean
        get() = selectedClassIdByDay.isNotEmpty()
}

val DayOfWeekKey.order: Int
    get() =
        when (this) {
            DayOfWeekKey.MONDAY -> 0
            DayOfWeekKey.TUESDAY -> 1
            DayOfWeekKey.WEDNESDAY -> 2
            DayOfWeekKey.THURSDAY -> 3
            DayOfWeekKey.FRIDAY -> 4
            DayOfWeekKey.SATURDAY -> 5
            DayOfWeekKey.SUNDAY -> 6
        }
