package com.ondot.domain.repository

import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>>
    suspend fun getPlaceHistory(): Flow<Result<List<PlaceHistory>>>
}