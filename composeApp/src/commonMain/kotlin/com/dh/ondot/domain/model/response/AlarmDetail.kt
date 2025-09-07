package com.dh.ondot.domain.model.response

import com.dh.ondot.domain.model.enums.AlarmMode
import com.dh.ondot.core.network.DefaultOnNullLongSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlarmDetail(
    @Serializable(with = DefaultOnNullLongSerializer::class)
    val alarmId: Long = -1,
    val alarmMode: AlarmMode = AlarmMode.SOUND,
    @SerialName("isEnabled")
    val enabled: Boolean = false,
    val triggeredAt: String = "2025-05-10T19:00:00",
    val isSnoozeEnabled: Boolean = false,
    val snoozeInterval: Int = 0,
    val snoozeCount: Int = 0,
    val soundCategory: String = "",
    val ringTone: String = "",
    val volume: Double = 0.0
)
