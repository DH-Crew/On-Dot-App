package com.dh.ondot.domain.model.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AlarmMode {
    @SerialName("SILENT") SILENT,
    @SerialName("VIBRATE") VIBRATE,
    @SerialName("SOUND") SOUND
}