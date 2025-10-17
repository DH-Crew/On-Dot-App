package com.ondot.data.mapper

import com.ondot.data.model.response.member.AddressResponse
import com.ondot.domain.model.member.AddressInfo
import com.ondot.network.base.Mapper

object AddressResponseMapper: Mapper<AddressResponse, AddressInfo> {
    override fun responseToModel(response: AddressResponse?): AddressInfo {
        return response?.let {
            AddressInfo(
                title = it.title,
                roadAddress = it.roadAddress,
                latitude = it.latitude,
                longitude = it.longitude
            )
        } ?: AddressInfo()
    }
}