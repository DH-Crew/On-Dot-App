package com.ondot.calendar.contract

import androidx.compose.runtime.Immutable
import com.ondot.domain.model.schedule.Schedule
import com.ondot.ui.base.UiState
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
private fun defaultDate(): LocalDate =
    Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

@Immutable
data class CalendarUiState(
    val currentMonth: CalendarMonth = defaultDate().let { CalendarMonth(it.year, it.month.number) },
    val selectedDate: LocalDate = defaultDate(),
    // 월 셀 점/칩용 상태 변수
    val schedulesByDate: Map<LocalDate, List<CalendarScheduleMarker>> = emptyMap(),
    // 바텀시트 상세용 상태 변수
    val selectedDateScheduleItems: List<CalendarScheduleItemUiModel> = emptyList(),
    // 알람 스케줄링, 취소를 위해서 바텀시트 일정 데이터 원본을 가지고 있어야 함
    val selectedDateSchedules: List<Schedule> = emptyList(),
    val togglingScheduleIds: Set<Long> = emptySet(),
) : UiState

@Immutable
data class CalendarMonth(
    val year: Int,
    val month: Int,
)

@Immutable
data class CalendarScheduleMarker(
    val title: String,
)

@Immutable
data class CalendarDayCell(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val isSelected: Boolean,
    val isToday: Boolean,
    val markers: List<CalendarScheduleMarker>,
)

@OptIn(ExperimentalTime::class)
fun CalendarUiState.toCalendarCells(): List<CalendarDayCell> {
    val today =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    val firstDateOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
    val firstDayOffset = firstDateOfMonth.dayOfWeek.sundayStartIndex()

    val prevMonth = currentMonth.previousMonth()
    val prevLastDate = LocalDate(prevMonth.year, prevMonth.month, 1).daysInMonth()

    val daysInMonth = firstDateOfMonth.daysInMonth()
    val nextMonth = currentMonth.nextMonth()

    return buildList {
        repeat(firstDayOffset) { index ->
            val day = prevLastDate - firstDayOffset + index + 1
            val date = LocalDate(prevMonth.year, prevMonth.month, day)
            add(
                CalendarDayCell(
                    date = date,
                    inCurrentMonth = false,
                    isSelected = false,
                    isToday = date == today,
                    markers = schedulesByDate[date].orEmpty(),
                ),
            )
        }

        repeat(daysInMonth) { index ->
            val day = index + 1
            val date = LocalDate(currentMonth.year, currentMonth.month, day)
            add(
                CalendarDayCell(
                    date = date,
                    inCurrentMonth = true,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    markers = schedulesByDate[date].orEmpty(),
                ),
            )
        }

        var nextDay = 1
        while (size % 7 != 0) {
            val date = LocalDate(nextMonth.year, nextMonth.month, nextDay++)
            add(
                CalendarDayCell(
                    date = date,
                    inCurrentMonth = false,
                    isSelected = false,
                    isToday = date == today,
                    markers = schedulesByDate[date].orEmpty(),
                ),
            )
        }
    }
}

fun CalendarMonth.previousMonth(): CalendarMonth = if (month == 1) CalendarMonth(year - 1, 12) else CalendarMonth(year, month - 1)

fun CalendarMonth.nextMonth(): CalendarMonth = if (month == 12) CalendarMonth(year + 1, 1) else CalendarMonth(year, month + 1)

private fun LocalDate.daysInMonth(): Int =
    when (month.number) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> error("Invalid month: ${month.number}")
    }

private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

private fun DayOfWeek.sundayStartIndex(): Int = isoDayNumber % 7

@OptIn(ExperimentalTime::class)
fun Schedule.toCalendarScheduleItemUiModel(
    selectedDate: LocalDate,
    nowDate: LocalDate =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date,
): CalendarScheduleItemUiModel {
    val appointmentTime = appointmentAt.substringAfter("T", "")
    val preparationTime = preparationAlarm.triggeredAt.substringAfter("T", "").take(5)
    val departureTime = departureAlarm.triggeredAt.substringAfter("T", "").take(5)
    val isPast = selectedDate < nowDate

    return CalendarScheduleItemUiModel(
        scheduleId = scheduleId,
        title = scheduleTitle,
        appointmentTimeText = appointmentTime.take(5),
        alarmInfoText = "준비 $preparationTime - 출발 $departureTime",
        isRepeat = isRepeat,
        isAlarmEnabled = hasActiveAlarm,
        isPast = isPast,
        repeatDays = repeatDays,
    )
}
