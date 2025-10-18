package com.ondot.data.mapper

import com.ondot.data.model.response.schedule.SchedulePreparationResponse
import com.ondot.domain.model.schedule.SchedulePreparation
import com.ondot.network.base.Mapper

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