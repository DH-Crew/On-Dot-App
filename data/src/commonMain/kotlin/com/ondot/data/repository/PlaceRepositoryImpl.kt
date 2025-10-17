package com.ondot.data.repository

import com.ondot.data.mapper.AddressListResponseMapper
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.repository.PlaceRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceRepositoryImpl(
    networkClient: NetworkClient
): PlaceRepository, BaseRepository(networkClient) {
    override suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>> = flow {
        emit(
            fetchMapped(
                method = HttpMethod.GET,
                path = "/places/search",
                query = mapOf("query" to query),
                mapper = AddressListResponseMapper
            )
        )
    }
}