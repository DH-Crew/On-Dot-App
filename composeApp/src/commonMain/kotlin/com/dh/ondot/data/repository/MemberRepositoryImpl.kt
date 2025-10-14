package com.dh.ondot.data.repository

import com.dh.ondot.core.network.BaseRepository
import com.dh.ondot.core.network.HttpMethod
import com.dh.ondot.core.network.NetworkClient
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.request.DeleteAccountRequest
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.ondot.domain.model.request.settings.preparation_time.PreparationTimeRequest
import com.ondot.domain.model.response.HomeAddressInfo
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.service.MapProviderStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    networkClient: NetworkClient,
    private val mapProviderStorage: MapProviderStorage
) : MemberRepository, BaseRepository(networkClient) {
    override suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<AuthTokens>> = flow {
        emit(fetch(HttpMethod.POST, "/members/onboarding", body = request))
    }

    override suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>> = flow {
        emit(fetch(HttpMethod.GET, "/members/home-address"))
    }

    override suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>> = flow {
        val result = fetch<Unit>(HttpMethod.DELETE, "/members", body = request)
        emit(result)

        // 성공했을 때만 local storage clear
        result.onSuccess { mapProviderStorage.clear() }
    }

    override suspend fun updateMapProvider(request: MapProviderRequest): Flow<Result<Unit>> = flow {
        val result = fetch<Unit>(HttpMethod.PATCH, "/members/map-provider", body = request)
        emit(result)

        // 성공했을 때만 local storage 업데이트
        result.onSuccess { mapProviderStorage.setMapProvider(request.mapProvider) }
    }

    override suspend fun updateHomeAddress(request: HomeAddressRequest): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.PATCH, "/members/home-address", body = request))
    }

    override suspend fun updatePreparationTime(request: PreparationTimeRequest): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.PATCH, "/members/preparation-time", body = request))
    }

    override fun getLocalMapProvider(): Flow<MapProvider> = flow {
        emitAll(mapProviderStorage.getMapProvider())
    }

    override suspend fun setLocalMapProvider(mapProvider: MapProvider) {
        mapProviderStorage.setMapProvider(mapProvider)
    }

    override fun needsChooseProvider(): Flow<Boolean> = flow {
        emitAll(mapProviderStorage.needsChooseProvider())
    }
}