package com.ondot.main

import com.ondot.domain.model.enums.BottomNavType
import com.ondot.ui.base.UiState

data class MainUiState(
    val bottomNavType: BottomNavType = BottomNavType.HOME
): UiState
