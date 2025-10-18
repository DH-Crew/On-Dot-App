package com.dh.ondot.presentation.main

import com.dh.ondot.core.ui.base.UiState
import com.ondot.domain.model.enums.BottomNavType

data class MainUiState(
    val bottomNavType: BottomNavType = BottomNavType.HOME
): UiState
