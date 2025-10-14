package com.dh.ondot.presentation.main

import com.dh.ondot.core.navigation.NavRoutes
import com.dh.ondot.core.ui.base.BaseViewModel
import com.ondot.domain.model.enums.BottomNavType

class MainViewModel(

) : BaseViewModel<MainUiState>(MainUiState()) {
    fun setBottomNavType(route: String) {
        val type = when(route) {
            NavRoutes.Home.route -> BottomNavType.HOME
            NavRoutes.Setting.route -> BottomNavType.SETTING
            else -> BottomNavType.HOME
        }

        updateBottomNavType(type)
    }

    private fun updateBottomNavType(type: BottomNavType) {
        updateState(uiState.value.copy(bottomNavType = type))
    }
}