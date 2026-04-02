package com.ondot.calendar.contract

import com.ondot.calendar.data.cache.CalendarDateScheduleCache
import com.ondot.ui.base.mvi.BaseViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

class CalendarViewModel : BaseViewModel<CalendarUiState, CalendarIntent, CalendarSideEffect>(CalendarUiState()) {
    private val dateScheduleCache =
        CalendarDateScheduleCache<String, List<CalendarScheduleItemUiModel>>(maxSize = 31)

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
            }

            is CalendarIntent.ToggleAlarm -> {
                // 추후 서버 반영 필요 시 optimistic update 추가
                reduce {
                    copy(
                        selectedDateScheduleItems =
                            selectedDateScheduleItems.map { item ->
                                if (item.scheduleId == intent.scheduleId) {
                                    item.copy(isAlarmEnabled = intent.enabled)
                                } else {
                                    item
                                }
                            },
                    )
                }

                val cacheKey = currentState.selectedDate.toApiDateString()
                dateScheduleCache[cacheKey] = currentState.selectedDateScheduleItems
            }
        }
    }

    // TODO: API 연동 이후 주석 해제
//    private fun loadSchedulesFor(date: LocalDate) {
//        val cacheKey = date.toApiDateString()
//        val cached = dateScheduleCache[cacheKey]
//
//        if (cached != null) {
//            reduce {
//                copy(
//                    selectedDateScheduleItems = cached,
//                    schedulesByDate =
//                        schedulesByDate + (date to cached.map { CalendarScheduleMarker(it.title) }),
//                )
//            }
//            return
//        }
//
//        launchResult(
//            block = {
//                calendarRepository.getSchedules(cacheKey)
//            },
//            onSuccess = { schedules ->
//                val items = schedules.map { it.toCalendarScheduleItemUiModel(date) }
//
//                dateScheduleCache[cacheKey] = items
//
//                reduce {
//                    copy(
//                        selectedDateScheduleItems = items,
//                        schedulesByDate =
//                            schedulesByDate + (date to items.map { CalendarScheduleMarker(it.title) }),
//                    )
//                }
//            },
//            onError = {
//                reduce {
//                    copy(
//                        selectedDateScheduleItems = emptyList(),
//                    )
//                }
//            },
//        )
//    }
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
