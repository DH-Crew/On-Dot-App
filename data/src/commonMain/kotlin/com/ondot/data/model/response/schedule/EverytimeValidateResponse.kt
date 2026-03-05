package com.ondot.data.model.response.schedule

import kotlinx.serialization.Serializable

@Serializable
data class EverytimeValidateResponse(
    val timetable: Map<String, List<TimetableEntryResponse>>,
)
