package com.dh.ondot.presentation.main

import com.dh.ondot.core.ui.base.BaseViewModel
import com.dh.ondot.domain.model.enums.BottomNavType

class MainViewModel(

) : BaseViewModel<MainUiState>(MainUiState()) {
    fun setBottomNavType(route: String) {
        val type = when(route) {
            // TODO("이후에 route가 추가되는 경우, route -> BottomNavType으로 변환하는 로직 추가")
            else -> BottomNavType.HOME
        }

        updateBottomNavType(type)
    }

    private fun updateBottomNavType(type: BottomNavType) {
        updateState(uiState.value.copy(bottomNavType = type))
    }
}