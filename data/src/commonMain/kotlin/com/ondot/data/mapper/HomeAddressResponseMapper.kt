package com.ondot.data.mapper

import com.ondot.data.model.response.member.HomeAddressResponse
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.network.base.Mapper

object HomeAddressResponseMapper : Mapper<HomeAddressResponse, HomeAddressInfo> {
    override fun responseToModel(response: HomeAddressResponse?): HomeAddressInfo =
        response?.let {
            HomeAddressInfo(
                roadAddress = it.roadAddress,
                latitude = it.latitude,
                longitude = it.longitude,
            )
        } ?: HomeAddressInfo()
}
