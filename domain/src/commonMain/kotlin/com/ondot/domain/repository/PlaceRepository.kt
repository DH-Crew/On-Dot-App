package com.dh.ondot.domain.repository

import com.dh.ondot.domain.model.response.AddressInfo
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>>
}