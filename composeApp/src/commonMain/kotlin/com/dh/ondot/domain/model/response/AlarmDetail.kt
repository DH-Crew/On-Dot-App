package com.dh.ondot.domain.model.response

import com.dh.ondot.domain.model.enums.AlarmMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlarmDetail(
    val alarmId: Long = -1,
    val alarmMode: AlarmMode = AlarmMode.SOUND,
    @SerialName("isEnabled")
    val enabled: Boolean? = null,
    val triggeredAt: String = "",
    val isSnoozeEnabled: Boolean = false,
    val snoozeInterval: Int = 0,
    val snoozeCount: Int = 0,
    val soundCategory: String = "",
    val ringTone: String = "",
    val volume: Double = 0.0
)
