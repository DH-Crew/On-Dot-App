package com.dh.ondot.domain.model.schedule

import kotlinx.serialization.Serializable

@Serializable
data class SchedulePreparation(
    val isMedicationRequired: Boolean = false,
    val preparationNote: String = ""
)
