package com.ondot.calendar.contract

import androidx.lifecycle.viewModelScope
import com.dh.ondot.presentation.ui.theme.ERROR_GET_SCHEDULE_LIST
import com.dh.ondot.presentation.ui.theme.SUCCESS_DELETE_SCHEDULE
import com.ondot.domain.model.enums.ToastType
import com.ondot.domain.model.schedule.Schedule
import com.ondot.domain.repository.CalendarRepository
import com.ondot.domain.repository.ScheduleRepository
import com.ondot.domain.service.ScheduleAlarmManager
import com.ondot.ui.base.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val scheduleRepository: ScheduleRepository,
    private val scheduleAlarmManager: ScheduleAlarmManager,
) : BaseViewModel<CalendarUiState, CalendarIntent, CalendarSideEffect>(CalendarUiState()) {
    private val togglingScheduleIds = mutableSetOf<Long>()

    private var markersJob: Job? = null
    private var schedulesJob: Job? = null
    private var markersRequestId: Long = 0L
    private var schedulesRequestId: Long = 0L

    override suspend fun handleIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.SelectDate -> {
                val selected = intent.date
                val targetMonth = CalendarMonth(selected.year, selected.month.number)

                reduce {
                    copy(
                        currentMonth = targetMonth,
                        selectedDate = selected,
                    )
                }

                loadSchedulesFor(selected)
            }

            CalendarIntent.MoveToPreviousMonth -> {
                val prevMonth = currentState.currentMonth.previousMonth()
                val nextSelectedDate =
                    currentState.selectedDate.moveToMonth(prevMonth)

                reduce {
                    copy(
                        currentMonth = prevMonth,
                        selectedDate = nextSelectedDate,
                    )
                }

                getScheduleMarkersInRange(nextSelectedDate)
                loadSchedulesFor(nextSelectedDate)
            }

            CalendarIntent.MoveToNextMonth -> {
                val nextMonth = currentState.currentMonth.nextMonth()
                val nextSelectedDate =
                    currentState.selectedDate.moveToMonth(nextMonth)

                reduce {
                    copy(
                        currentMonth = nextMonth,
                        selectedDate = nextSelectedDate,
                    )
                }

                getScheduleMarkersInRange(nextSelectedDate)
                loadSchedulesFor(nextSelectedDate)
            }

            is CalendarIntent.ToggleAlarm -> {
                toggleAlarm(
                    scheduleId = intent.scheduleId,
                    enabled = intent.enabled,
                )
            }

            CalendarIntent.Init -> {
                getScheduleMarkersInRange(currentState.selectedDate)
                loadSchedulesFor(currentState.selectedDate)
            }

            is CalendarIntent.DeleteHistory -> {
                if (intent.isPast) {
                    deleteHistory(intent.scheduleId)
                } else {
                    deleteSchedule(intent.scheduleId)
                }
            }
        }
    }

    private fun getScheduleMarkersInRange(currentDate: LocalDate) {
        // 동시에 여러 요청이 발생했을 때 뒤늦은 응답이 상태를 잘못 덮어쓰지 않도록 방어
        val requestedMonth = CalendarMonth(currentDate.year, currentDate.month.number)
        val requestId = ++markersRequestId

        val firstDateOfMonth = LocalDate(currentDate.year, currentDate.month.number, 1)
        val lastDateOfMonth =
            LocalDate(
                currentDate.year,
                currentDate.month.number,
                daysInMonth(currentDate.year, currentDate.month.number),
            )
        val startDate = firstDateOfMonth.plus(DatePeriod(days = -7))
        val endDate = lastDateOfMonth.plus(DatePeriod(days = 7))

        markersJob?.cancel()
        markersJob =
            launchResult(
                block = {
                    calendarRepository.getScheduleMarkersInRange(
                        startDate = startDate.toApiDateString(),
                        endDate = endDate.toApiDateString(),
                    )
                },
                onSuccess = onSuccess@{ summaries ->
                    if (requestId != markersRequestId) return@onSuccess
                    if (currentState.currentMonth != requestedMonth) return@onSuccess

                    val schedulesByDate =
                        summaries.associate { summary ->
                            summary.date to
                                summary.schedules.map { schedule ->
                                    CalendarScheduleMarker(scheduleId = schedule.scheduleId, title = schedule.title)
                                }
                        }

                    reduce {
                        copy(
                            schedulesByDate = schedulesByDate,
                        )
                    }
                },
                onError = onError@{
                    if (requestId != markersRequestId) return@onError
                    if (currentState.currentMonth != requestedMonth) return@onError

                    reduce {
                        copy(
                            schedulesByDate = emptyMap(),
                        )
                    }
                    emitEffect(CalendarSideEffect.ShowToast(ERROR_GET_SCHEDULE_LIST, ToastType.ERROR))
                },
            )
    }

    private fun loadSchedulesFor(date: LocalDate) {
        val key = date.toApiDateString()
        val requestId = ++schedulesRequestId

        schedulesJob?.cancel()
        schedulesJob =
            launchResult(
                block = {
                    calendarRepository.getSchedulesFor(key)
                },
                onSuccess = onSuccess@{ schedules ->
                    if (requestId != schedulesRequestId) return@onSuccess
                    if (currentState.selectedDate != date) return@onSuccess

                    reduce {
                        copy(
                            selectedDateSchedules = schedules,
                            selectedDateScheduleItems = schedules.map { it.toCalendarScheduleItemUiModel(date) },
                        )
                    }
                },
                onError = onError@{
                    if (requestId != schedulesRequestId) return@onError
                    if (currentState.selectedDate != date) return@onError

                    reduce {
                        copy(
                            selectedDateSchedules = emptyList(),
                            selectedDateScheduleItems = emptyList(),
                        )
                    }
                    emitEffect(CalendarSideEffect.ShowToast(ERROR_GET_SCHEDULE_LIST, ToastType.ERROR))
                },
            )
    }

    // --------------------------------------------알람 스케줄링, 취소------------------------------------------------

    private fun toggleAlarm(
        scheduleId: Long,
        enabled: Boolean,
    ) {
        if (!togglingScheduleIds.add(scheduleId)) return

        reduce {
            copy(
                togglingScheduleIds = togglingScheduleIds.toSet(),
            )
        }

        val targetDate = currentState.selectedDate
        val originalSchedules = currentState.selectedDateSchedules
        val originalSchedule =
            originalSchedules.firstOrNull { it.scheduleId == scheduleId }
                ?: run {
                    togglingScheduleIds.remove(scheduleId)
                    reduce {
                        copy(
                            togglingScheduleIds = togglingScheduleIds.toSet(),
                        )
                    }
                    return
                }

        val updatedSchedule =
            originalSchedule.copy(
                hasActiveAlarm = enabled,
                preparationAlarm = originalSchedule.preparationAlarm.copy(enabled = enabled),
                departureAlarm = originalSchedule.departureAlarm.copy(enabled = enabled),
            )

        val updatedSchedules =
            originalSchedules.map { schedule ->
                if (schedule.scheduleId == scheduleId) updatedSchedule else schedule
            }

        applySchedules(
            date = targetDate,
            schedules = updatedSchedules,
        )

        viewModelScope.launch {
            scheduleRepository.upsertLocalSchedule(updatedSchedule)
        }

        viewModelScope.launch {
            scheduleAlarmManager.applyToggle(
                original = originalSchedule,
                enabled = enabled,
            )
        }

        launchResult(
            block = {
                scheduleRepository.toggleAlarm(
                    scheduleId = scheduleId,
                    isEnabled = enabled,
                )
            },
            onError = {
                scheduleRepository.upsertLocalSchedule(originalSchedule)
                applySchedules(
                    date = targetDate,
                    schedules = originalSchedules,
                )

                viewModelScope.launch {
                    scheduleAlarmManager.applyToggle(
                        original = originalSchedule,
                        enabled = originalSchedule.hasActiveAlarm,
                    )
                }
            },
            onSuccess = {},
            onFinally = {
                togglingScheduleIds.remove(scheduleId)
                reduce {
                    copy(
                        togglingScheduleIds = togglingScheduleIds.toSet(),
                    )
                }
            },
        )
    }

    private fun applySchedules(
        date: LocalDate,
        schedules: List<Schedule>,
    ) {
        if (currentState.selectedDate != date) return

        reduce {
            copy(
                selectedDateSchedules = schedules,
                selectedDateScheduleItems = schedules.map { it.toCalendarScheduleItemUiModel(date) },
            )
        }
    }

    private fun cancelScheduleAlarms(schedule: Schedule) {
        viewModelScope.launch { scheduleAlarmManager.cancel(schedule) }
    }

    // --------------------------------------------과거 기록 삭제------------------------------------------------

    private fun deleteHistory(scheduleId: Long) {
        val selectedDate = currentState.selectedDate
        val date = selectedDate.toApiDateString()

        val previousItems = currentState.selectedDateScheduleItems
        val previousSchedulesByDate = currentState.schedulesByDate

        val updatedItems = previousItems.filter { it.scheduleId != scheduleId }
        if (previousItems.size == updatedItems.size) return

        val previousMarkers = previousSchedulesByDate[selectedDate].orEmpty()
        val updatedMarkers = previousMarkers.filter { it.scheduleId != scheduleId }

        val updatedSchedulesByDate =
            previousSchedulesByDate.toMutableMap().apply {
                if (updatedMarkers.isEmpty()) {
                    remove(selectedDate)
                } else {
                    this[selectedDate] = updatedMarkers
                }
            }

        reduce {
            copy(
                selectedDateScheduleItems = updatedItems,
                schedulesByDate = updatedSchedulesByDate,
            )
        }

        launchResult(
            block = { calendarRepository.deleteHistory(scheduleId, date) },
            onSuccess = {},
            onError = {
                reduce {
                    copy(
                        selectedDateScheduleItems = previousItems,
                        schedulesByDate = previousSchedulesByDate,
                    )
                }
            },
        )
    }

    // --------------------------------------------일정 삭제------------------------------------------------

    private fun deleteSchedule(scheduleId: Long) {
        val selectedDate = currentState.selectedDate
        val previousSchedules = currentState.selectedDateSchedules
        val previousItems = currentState.selectedDateScheduleItems
        val previousSchedulesByDate = currentState.schedulesByDate

        val targetSchedule =
            previousSchedules.firstOrNull { it.scheduleId == scheduleId }
                ?: run {
                    logger.e { "일정을 찾을 수 없습니다. $scheduleId" }
                    return
                }

        val updatedSchedules = previousSchedules.filter { it.scheduleId != scheduleId }
        val updatedItems = previousItems.filter { it.scheduleId != scheduleId }

        val previousMarkersForDate = previousSchedulesByDate[selectedDate].orEmpty()
        val updatedMarkersForDate = previousMarkersForDate.filter { it.scheduleId != scheduleId }

        val updatedSchedulesByDate =
            previousSchedulesByDate.toMutableMap().apply {
                if (updatedMarkersForDate.isEmpty()) {
                    remove(selectedDate)
                } else {
                    this[selectedDate] = updatedMarkersForDate
                }
            }

        cancelScheduleAlarms(targetSchedule)

        reduce {
            copy(
                selectedDateSchedules = updatedSchedules,
                selectedDateScheduleItems = updatedItems,
                schedulesByDate = updatedSchedulesByDate,
            )
        }

        launchResult(
            block = { scheduleRepository.deleteScheduleAppResult(scheduleId) },
            onSuccess = {
                emitEffect(CalendarSideEffect.ShowToast(SUCCESS_DELETE_SCHEDULE, ToastType.INFO))
            },
            onError = {
                applySchedules(
                    date = selectedDate,
                    schedules = previousSchedules,
                )

                reduce {
                    copy(
                        schedulesByDate = previousSchedulesByDate,
                    )
                }

                viewModelScope.launch {
                    if (targetSchedule.hasActiveAlarm) {
                        scheduleAlarmManager.schedule(targetSchedule)
                    }
                }
            },
        )
    }
}

private fun LocalDate.moveToMonth(targetMonth: CalendarMonth): LocalDate {
    val targetDay = day.coerceAtMost(daysInMonth(targetMonth.year, targetMonth.month))
    return LocalDate(targetMonth.year, targetMonth.month, targetDay)
}

private fun LocalDate.toApiDateString(): String =
    "$year-${month.number.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"

private fun daysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> error("Invalid month: $month")
    }

private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
