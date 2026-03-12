package com.ondot.data.model.response.member.mapper

import com.ondot.data.model.response.member.HomeAddressResponse
import com.ondot.data.model.response.member.PlaceHistoryResponse
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PlaceHistory

fun HomeAddressResponse.toDomain(): HomeAddressInfo =
    HomeAddressInfo(
        roadAddress = roadAddress,
        latitude = latitude,
        longitude = longitude,
    )

fun PlaceHistoryResponse.toDomain(): PlaceHistory =
    PlaceHistory(
        title = title,
        roadAddress = roadAddress,
        latitude = latitude,
        longitude = longitude,
        searchedAt = searchedAt,
    )
