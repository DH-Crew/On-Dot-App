package com.dh.ondot.presentation.edit.bottomSheet

import com.dh.ondot.core.ui.base.BaseViewModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class EditBottomSheetViewModel(

) : BaseViewModel<EditBottomSheetUiState>(EditBottomSheetUiState()) {
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)

    /**---------------------------------------------Date-----------------------------------------------*/

    fun initDate(isRepeat: Boolean, repeatDays: Set<Int>, currentDate: LocalDate) {
        updateState(
            uiState.value.copy(isRepeat = isRepeat, repeatDays = repeatDays, currentDate = currentDate)
        )
    }

    fun onClickSwitch(newValue: Boolean) {
        updateState(uiState.value.copy(isRepeat = newValue))

        if (!newValue) {
            updateState(
                uiState.value.copy(
                    activeCheckChip = null,
                    repeatDays = emptySet()
                )
            )
        } else {
            updateState(uiState.value.copy(currentDate = null))
        }
    }

    fun onClickCheckTextChip(index: Int) {
        updateState(
            uiState.value.copy(
                activeCheckChip = index,
                repeatDays = when (index) {
                    0 -> fullWeek.toSet()
                    1 -> weekDays.toSet()
                    2 -> weekend.toSet()
                    else -> emptySet()
                }
            )
        )
    }

    fun onClickTextChip(index: Int) {
        val activeWeekDays = uiState.value.repeatDays.toMutableSet()

        if (activeWeekDays.contains(index)) activeWeekDays.remove(index)
        else activeWeekDays.add(index)

        updateState(uiState.value.copy(
            repeatDays = activeWeekDays,
            activeCheckChip = when (activeWeekDays) {
                fullWeek.toSet() -> 0
                weekDays.toSet()  -> 1
                weekend.toSet()  -> 2
                else -> null
            }
        ))
    }

    fun onPrevMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.minus(DatePeriod(months = 1))))
    }

    fun onNextMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.plus(DatePeriod(months = 1))))
    }

    fun onDateSelected(date: LocalDate) {
        updateState(uiState.value.copy(currentDate = date))
    }

    /**---------------------------------------------Time-----------------------------------------------*/

    fun clear() {
        updateState(EditBottomSheetUiState())
    }
}