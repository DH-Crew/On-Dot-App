package com.ondot.main

import com.ondot.domain.model.enums.BottomNavType
import com.ondot.domain.service.AnalyticsManager
import com.ondot.navigation.NavRoutes
import com.ondot.ui.base.BaseViewModel

class MainViewModel(
    private val analyticsManager: AnalyticsManager,
) : BaseViewModel<MainUiState>(MainUiState()) {
    fun setBottomNavType(route: String) {
        val type =
            when (route) {
                NavRoutes.Home.route -> BottomNavType.HOME
                NavRoutes.Calendar.route -> BottomNavType.CALENDAR
                NavRoutes.Setting.route -> BottomNavType.SETTING
                else -> BottomNavType.HOME
            }

        when (route) {
            NavRoutes.Home.route -> analyticsManager.logEvent("screen_view_home")
            NavRoutes.Calendar.route -> analyticsManager.logEvent("screen_view_calendar")
            NavRoutes.Setting.route -> analyticsManager.logEvent("screen_view_setting")
        }

        updateBottomNavType(type)
    }

    private fun updateBottomNavType(type: BottomNavType) {
        updateState(uiState.value.copy(bottomNavType = type))
    }
}
