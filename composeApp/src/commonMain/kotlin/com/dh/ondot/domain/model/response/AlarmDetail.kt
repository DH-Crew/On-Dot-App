package com.dh.ondot.domain.model.response

import com.dh.ondot.domain.model.enums.AlarmMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlarmDetail(
    val alarmId: Long,
    val alarmMode: AlarmMode,
    @SerialName("isEnabled")
    val enabled: Boolean? = null,
    val triggeredAt: String,
    val isSnoozeEnabled: Boolean,
    val snoozeInterval: Int,
    val snoozeCount: Int,
    val soundCategory: String,
    val ringTone: String,
    val volume: Double
)
