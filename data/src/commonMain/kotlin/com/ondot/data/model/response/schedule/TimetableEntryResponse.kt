package com.ondot.data.model.response.schedule

import com.ondot.network.serializer.LocalTimeStringSerializer
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class TimetableEntryResponse(
    val courseName: String,
    @Serializable(with = LocalTimeStringSerializer::class)
    val startTime: LocalTime,
    @Serializable(with = LocalTimeStringSerializer::class)
    val endTime: LocalTime,
)
