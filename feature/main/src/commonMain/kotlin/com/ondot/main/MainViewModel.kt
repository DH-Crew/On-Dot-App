package com.ondot.main

import com.ondot.domain.model.enums.BottomNavType
import com.ondot.navigation.NavRoutes
import com.ondot.ui.base.BaseViewModel

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