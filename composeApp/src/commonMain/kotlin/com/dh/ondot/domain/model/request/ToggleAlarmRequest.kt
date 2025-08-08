package com.dh.ondot.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ToggleAlarmRequest(
    val isEnabled: Boolean
)
