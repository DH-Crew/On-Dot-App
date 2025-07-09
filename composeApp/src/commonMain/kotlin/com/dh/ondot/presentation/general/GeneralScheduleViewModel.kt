package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.domain.repository.ScheduleRepository
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class GeneralScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel<GeneralScheduleUiState>(GeneralScheduleUiState()) {
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)

    fun initStep() {
        updateState(uiState.value.copy(totalStep = 2, currentStep = 1))
    }

    /**--------------------------------------------RepeatSettingSection-----------------------------------------------*/

    fun onClickSwitch(newValue: Boolean) {
        updateState(uiState.value.copy(isRepeat = newValue))

        if (!newValue) {
            updateState(
                uiState.value.copy(
                    activeCheckChip = null,
                    activeWeekDays = emptySet()
                )
            )
        }
    }

    fun onClickCheckTextChip(index: Int) {
        updateState(
            uiState.value.copy(
                activeCheckChip = index,
                isActiveCalendar = true,
                activeWeekDays = when (index) {
                    0 -> fullWeek.toSet()
                    1 -> weekDays.toSet()
                    2 -> weekend.toSet()
                    else -> emptySet()
                }
            )
        )
    }

    fun onClickTextChip(index: Int) {
        val activeWeekDays = uiState.value.activeWeekDays.toMutableSet()

        if (activeWeekDays.contains(index)) activeWeekDays.remove(index)
        else activeWeekDays.add(index)

        updateState(uiState.value.copy(
            isActiveCalendar = true,
            activeWeekDays = activeWeekDays,
            activeCheckChip = when (activeWeekDays) {
                fullWeek.toSet() -> 0
                weekDays.toSet()  -> 1
                weekend.toSet()  -> 2
                else -> null
            }
        ))
    }

    /**--------------------------------------------DateSettingSection-----------------------------------------------*/

    fun onToggleCalendar() {
        updateState(uiState.value.copy(isActiveCalendar = !uiState.value.isActiveCalendar))
    }

    fun onPrevMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.minus(DatePeriod(months = 1))))
    }

    fun onNextMonth() {
        updateState(uiState.value.copy(calendarMonth = uiState.value.calendarMonth.plus(DatePeriod(months = 1))))
    }

    fun onSelectDate(date: LocalDate) {
        updateState(uiState.value.copy(selectedDate = date))
    }

    fun onToggleDial() {
        updateState(uiState.value.copy(isActiveDial = !uiState.value.isActiveDial))
    }

    fun onTimeSelected(newTime: LocalTime) {
        updateState(uiState.value.copy(selectedTime = newTime))
    }

    /**--------------------------------------------ETC-----------------------------------------------*/

    fun isButtonEnabled(): Boolean {
        return when (uiState.value.currentStep) {
            1 -> uiState.value.selectedTime != null && (uiState.value.selectedDate != null || uiState.value.activeWeekDays.isNotEmpty())
            2 -> false
            else -> false
        }
    }

    fun onClickNextButton() {
        updateState(uiState.value.copy(currentStep = uiState.value.currentStep + 1))
        emitEventFlow(GeneralScheduleEvent.NavigateToPlacePicker)
    }
}