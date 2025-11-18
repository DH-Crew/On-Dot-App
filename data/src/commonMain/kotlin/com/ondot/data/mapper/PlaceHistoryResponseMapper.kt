package com.ondot.data.mapper

import com.ondot.data.model.response.member.PlaceHistoryResponse
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.network.base.Mapper

object PlaceHistoryResponseMapper: Mapper<List<PlaceHistoryResponse>, List<PlaceHistory>> {
    override fun responseToModel(response: List<PlaceHistoryResponse>?): List<PlaceHistory> {
        return response?.let { list ->
            list.map {
                PlaceHistory(
                    title = it.title,
                    roadAddress = it.roadAddress,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    searchedAt = it.searchedAt
                )
            }
        } ?: emptyList()
    }
}