package com.ondot.domain.repository

import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.request.DeletePlaceHistoryRequest
import com.ondot.result.AppResult
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>>

    suspend fun getPlaceHistory(): Flow<Result<List<PlaceHistory>>>

    suspend fun savePlaceHistory(place: AddressInfo): Flow<Result<Unit>>

    suspend fun deletePlaceHistory(request: DeletePlaceHistoryRequest): Flow<Result<Unit>>

    // -----------MVI
    suspend fun deleteHistory(searchedAt: String): AppResult<Unit>

    suspend fun fetchHistory(): AppResult<List<PlaceHistory>>

    suspend fun saveHistory(place: AddressInfo): AppResult<Unit>
}
