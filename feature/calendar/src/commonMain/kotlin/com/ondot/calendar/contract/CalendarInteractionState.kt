package com.ondot.calendar.contract

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

enum class MonthTransitionDirection {
    Previous,
    Next,
}

enum class GestureAxis {
    Horizontal,
    Vertical,
}

@Stable
class CalendarInteractionState(
    private val currentMonthProvider: () -> CalendarMonth,
    private val sheetState: CalendarSheetState,
    private val onSelectDate: (LocalDate) -> Unit,
    private val onMoveToPreviousMonth: () -> Unit,
    private val onMoveToNextMonth: () -> Unit,
    private val onShowPeekSheet: () -> Unit,
) {
    var transitionDirection by mutableStateOf(MonthTransitionDirection.Next)
        private set

    fun onDateClick(clickedDate: LocalDate) {
        val currentMonth = currentMonthProvider()
        val clickedMonth = CalendarMonth(clickedDate.year, clickedDate.month.number)

        transitionDirection =
            when {
                clickedMonth.year > currentMonth.year -> MonthTransitionDirection.Next
                clickedMonth.year < currentMonth.year -> MonthTransitionDirection.Previous
                clickedMonth.month > currentMonth.month -> MonthTransitionDirection.Next
                clickedMonth.month < currentMonth.month -> MonthTransitionDirection.Previous
                else -> transitionDirection
            }

        onSelectDate(clickedDate)

        if (sheetState.currentAnchor == CalendarSheetAnchor.Hidden) {
            onShowPeekSheet()
        }
    }

    fun onClickPreviousMonth() {
        transitionDirection = MonthTransitionDirection.Previous
        onMoveToPreviousMonth()
    }

    fun onClickNextMonth() {
        transitionDirection = MonthTransitionDirection.Next
        onMoveToNextMonth()
    }

    fun handleHorizontalSwipe(
        totalDragX: Float,
        containerWidthPx: Float,
    ) {
        if (containerWidthPx == 0f) return

        val threshold = containerWidthPx * 0.18f

        when {
            totalDragX < -threshold -> {
                transitionDirection = MonthTransitionDirection.Next
                onMoveToNextMonth()
            }
            totalDragX > threshold -> {
                transitionDirection = MonthTransitionDirection.Previous
                onMoveToPreviousMonth()
            }
        }
    }

    fun handleVerticalDrag(
        deltaY: Float,
        containerHeightPx: Float,
    ) {
        if (containerHeightPx == 0f) return
        val deltaWeight = -deltaY / containerHeightPx
        sheetState.snapBy(deltaWeight)
    }
}
