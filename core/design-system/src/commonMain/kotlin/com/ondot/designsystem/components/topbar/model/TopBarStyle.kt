package com.ondot.designsystem.components.topbar.model

sealed interface TopBarStyle {
    data object CloseOnly : TopBarStyle

    data object CloseTitleEdit : TopBarStyle

    data class BackCenterTitle(
        val title: String,
    ) : TopBarStyle
}
