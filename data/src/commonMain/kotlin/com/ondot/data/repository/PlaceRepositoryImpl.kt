package com.ondot.data.repository

import com.ondot.data.mapper.AddressListResponseMapper
import com.ondot.data.mapper.PlaceHistoryResponseMapper
import com.ondot.data.model.response.member.PlaceHistoryResponse
import com.ondot.data.model.response.member.mapper.toDomain
import com.ondot.domain.model.member.AddressInfo
import com.ondot.domain.model.member.PlaceHistory
import com.ondot.domain.model.request.DeletePlaceHistoryRequest
import com.ondot.domain.repository.PlaceRepository
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import com.ondot.network.execute.safeApiCall
import com.ondot.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaceRepositoryImpl(
    private val networkClient: NetworkClient,
) : BaseRepository(networkClient),
    PlaceRepository {
    override suspend fun searchPlace(query: String): Flow<Result<List<AddressInfo>>> =
        flow {
            emit(
                fetchMapped(
                    method = HttpMethod.GET,
                    path = "/places/search",
                    query = mapOf("query" to query),
                    mapper = AddressListResponseMapper,
                ),
            )
        }

    override suspend fun getPlaceHistory(): Flow<Result<List<PlaceHistory>>> =
        flow {
            emit(fetchMapped(HttpMethod.GET, "/places/history", mapper = PlaceHistoryResponseMapper))
        }

    override suspend fun savePlaceHistory(place: AddressInfo): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.POST, "/places/history", body = place))
        }

    override suspend fun deletePlaceHistory(request: DeletePlaceHistoryRequest): Flow<Result<Unit>> =
        flow {
            emit(fetch(HttpMethod.DELETE, "/places/history", body = request))
        }

    override suspend fun deleteHistory(searchedAt: String): AppResult<Unit> =
        safeApiCall {
            networkClient.requestOrThrow(
                method = HttpMethod.DELETE,
                path = "/places/history",
                body = DeletePlaceHistoryRequest(searchedAt = searchedAt),
            )
        }

    override suspend fun fetchHistory(): AppResult<List<PlaceHistory>> =
        safeApiCall {
            networkClient
                .requestOrThrow<List<PlaceHistoryResponse>>(
                    method = HttpMethod.GET,
                    path = "/places/history",
                ).map {
                    it.toDomain()
                }
        }

    override suspend fun saveHistory(place: AddressInfo): AppResult<Unit> =
        safeApiCall {
            networkClient.requestOrThrow(
                method = HttpMethod.POST,
                path = "/places/history",
                body = place,
            )
        }
}
