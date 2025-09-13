package com.dh.ondot.data.model.response.schedule

import kotlinx.serialization.Serializable

@Serializable
data class SchedulePreparationResponse(
    val isMedicationRequired: Boolean = false,
    val preparationNote: String = ""
)