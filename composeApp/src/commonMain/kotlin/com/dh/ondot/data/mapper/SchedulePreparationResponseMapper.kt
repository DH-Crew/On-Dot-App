package com.dh.ondot.data.mapper

import com.dh.ondot.data.base.Mapper
import com.dh.ondot.data.model.response.schedule.SchedulePreparationResponse
import com.ondot.domain.model.schedule.SchedulePreparation

object SchedulePreparationResponseMapper: Mapper<SchedulePreparationResponse, SchedulePreparation> {
    override fun responseToModel(response: SchedulePreparationResponse?): SchedulePreparation {
        return response?.let {
            SchedulePreparation(
                isMedicationRequired = it.isMedicationRequired,
                preparationNote = it.preparationNote
            )
        } ?: SchedulePreparation()
    }
}