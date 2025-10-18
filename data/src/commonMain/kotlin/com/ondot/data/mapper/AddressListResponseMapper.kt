package com.ondot.data.mapper

import com.ondot.data.model.response.member.AddressResponse
import com.ondot.domain.model.member.AddressInfo
import com.ondot.network.base.Mapper

object AddressListResponseMapper: Mapper<List<AddressResponse>, List<AddressInfo>> {
    override fun responseToModel(response: List<AddressResponse>?): List<AddressInfo> {
        return response?.let { list ->
            list.map {
                AddressResponseMapper.responseToModel(it)
            }
        } ?: emptyList()
    }
}