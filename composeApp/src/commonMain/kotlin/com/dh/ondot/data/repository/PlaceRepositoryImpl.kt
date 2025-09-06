package com.dh.ondot.data.repository

import com.dh.ondot.domain.model.response.AddressInfo
import com.dh.ondot.domain.repository.PlaceRepository
import com.dh.ondot.core.network.HttpMethod
import com.dh.ondot.core.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceRepositoryImpl(
    private val networkClient: NetworkClient
): PlaceRepository {
    override suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>> = flow {
        val response = networkClient.request<List<AddressInfo>>(
            method = HttpMethod.GET,
            path = "/places/search",
            queryParams = mapOf("query" to query)
        )

        emit(response.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        ))
    }
}