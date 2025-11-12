package com.ondot.data.repository

import com.ondot.data.mapper.HomeAddressResponseMapper
import com.ondot.data.mapper.PreparationTimeResponseMapper
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.request.DeleteAccountRequest
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.ondot.domain.model.request.settings.preparation_time.PreparationTimeRequest
import com.ondot.domain.model.member.HomeAddressInfo
import com.ondot.domain.model.member.PreparationTime
import com.ondot.domain.repository.MemberRepository
import com.ondot.domain.service.MapProviderStorage
import com.ondot.domain.service.TokenProvider
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    private val networkClient: NetworkClient,
    private val mapProviderStorage: MapProviderStorage,
    private val tokenProvider: TokenProvider
): MemberRepository, BaseRepository(networkClient) {

    override suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<AuthTokens>> = flow {
        emit(fetch(HttpMethod.POST, "/members/onboarding", body = request))
    }

    override suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>> = flow {
        emit(fetchMapped(HttpMethod.GET, "/members/home-address", mapper = HomeAddressResponseMapper))
    }

    override suspend fun getPreparationTime(): Flow<Result<PreparationTime>> = flow {
        emit(fetchMapped(HttpMethod.GET, "/members/preparation-time", mapper = PreparationTimeResponseMapper))
    }

    override suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>> = flow {
        val result = fetch<Unit>(HttpMethod.DELETE, "/members", body = request)
        emit(result)

        // 성공했을 때만 local storage clear
        if (result.isSuccess) {
            tokenProvider.clearToken()
            mapProviderStorage.clear()
        }
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