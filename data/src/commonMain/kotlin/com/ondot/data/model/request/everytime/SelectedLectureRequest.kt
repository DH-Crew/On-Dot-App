package com.ondot.data.model.request.everytime

import com.ondot.network.serializer.LocalTimeStringSerializer
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class SelectedLectureRequest(
    val day: String,
    @Serializable(with = LocalTimeStringSerializer::class)
    val startTime: LocalTime,
)
