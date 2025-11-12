package com.ondot.data.mapper

import com.ondot.data.model.response.member.PreparationTimeResponse
import com.ondot.domain.model.member.PreparationTime
import com.ondot.network.base.Mapper

object PreparationTimeResponseMapper: Mapper<PreparationTimeResponse, PreparationTime> {
    override fun responseToModel(response: PreparationTimeResponse?): PreparationTime {
        return response?.let {
            PreparationTime(
                preparationTime = it.preparationTime
            )
        } ?: PreparationTime()
    }
}