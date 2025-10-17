package com.ondot.data.model.response.alarm

import com.ondot.domain.model.enums.AlarmMode
import kotlinx.serialization.Serializable

@Serializable
data class AlarmResponse(
    val alarmId: Long = -1,
    val alarmMode: AlarmMode = AlarmMode.SOUND,
    val enabled: Boolean = false,
    val triggeredAt: String = "2025-05-10T19:00:00",
    val isSnoozeEnabled: Boolean = false,
    val snoozeInterval: Int = 0,
    val snoozeCount: Int = 0,
    val soundCategory: String = "",
    val ringTone: String = "",
    val volume: Double = 0.0
)
