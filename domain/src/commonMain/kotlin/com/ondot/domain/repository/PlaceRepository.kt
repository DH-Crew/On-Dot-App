package com.ondot.domain.repository

import com.ondot.domain.model.response.AddressInfo
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>>
}