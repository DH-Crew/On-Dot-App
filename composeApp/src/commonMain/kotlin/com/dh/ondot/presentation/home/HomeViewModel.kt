package com.dh.ondot.presentation.home

import com.dh.ondot.core.ui.base.BaseViewModel

class HomeViewModel(

) : BaseViewModel<HomeUiState>(HomeUiState()) {
    fun onToggle() {
        updateState(uiState.value.copy(isExpanded = !uiState.value.isExpanded))
    }

    fun onClickAlarmSwitch(id: Long, isEnabled: Boolean) {
        updateState(uiState.value.copy(scheduleList = uiState.value.scheduleList.map {
            if (it.scheduleId == id) {
                it.copy(isEnabled = isEnabled)
            } else {
                it
            }
        }))
    }
}