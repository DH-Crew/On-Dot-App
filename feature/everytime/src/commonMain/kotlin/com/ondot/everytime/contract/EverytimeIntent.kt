package com.ondot.everytime.contract

import com.ondot.ui.base.mvi.Intent

sealed interface EverytimeIntent : Intent {
    data class Validate(
        val url: String,
    ) : EverytimeIntent
}
