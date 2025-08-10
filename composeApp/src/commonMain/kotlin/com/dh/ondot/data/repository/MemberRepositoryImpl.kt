package com.dh.ondot.data.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.model.request.DeleteAccountRequest
import com.dh.ondot.domain.model.request.MapProviderRequest
import com.dh.ondot.domain.model.request.OnboardingRequest
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.domain.service.MapProviderStorage
import com.dh.ondot.network.HttpMethod
import com.dh.ondot.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapProviderStorage: MapProviderStorage
) : MemberRepository {
    override suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<TokenModel>> = flow {
        val response = networkClient.request<TokenModel>(
            path = "/members/onboarding",
            method = HttpMethod.POST,
            body = request
        )

        response.fold(
            onSuccess = { emit(Result.success(it)) },
            onFailure = { emit(Result.failure(it)) }
        )
    }

    override suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>> = flow {
        val response = networkClient.request<HomeAddressInfo>(
            path = "/members/home-address",
            method = HttpMethod.GET
        )

        response.fold(
            onSuccess = { emit(Result.success(it)) },
            onFailure = { emit(Result.failure(it)) }
        )
    }

    override suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>> = flow {
        val response = networkClient.request<Unit>(
            path = "/members/deactivate",
            method = HttpMethod.POST,
            body = request
        )

        response.fold(
            onSuccess = {
                emit(Result.success(it))
                mapProviderStorage.clear()
            },
            onFailure = { emit(Result.failure(it)) }
        )
    }

    override suspend fun updateMapProvider(request: MapProviderRequest): Flow<Result<Unit>> = flow {
        mapProviderStorage.setMapProvider(request.mapProvider)

        val response = networkClient.request<Unit>(
            path = "/members/map-provider",
            method = HttpMethod.PATCH,
            body = request
        )

        response.fold(
            onSuccess = { emit(Result.success(it)) },
            onFailure = { emit(Result.failure(it)) }
        )
    }

    override fun getLocalMapProvider(): Flow<MapProvider> = flow {
        emitAll(mapProviderStorage.getMapProvider())
    }

    override fun needsChooseProvider(): Flow<Boolean> = flow {
        emitAll(mapProviderStorage.needsChooseProvider())
    }
}