package com.ondot.domain.model.schedule

data class SchedulePreparation(
    val isMedicationRequired: Boolean = false,
    val preparationNote: String = ""
)
