package com.dh.ondot.presentation.general

import com.dh.ondot.core.ui.base.BaseViewModel

class GeneralScheduleViewModel(

) : BaseViewModel<GeneralScheduleUiState>(GeneralScheduleUiState()) {
    private val fullWeek = (0..6).toList()
    private val weekDays = (1..5).toList()
    private val weekend = listOf(0, 6)

    fun initStep() {
        updateState(uiState.value.copy(totalStep = 2, currentStep = 1))
    }

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
}