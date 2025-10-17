package com.ondot.domain.repository

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.request.DeleteAccountRequest
import com.ondot.domain.model.request.MapProviderRequest
import com.ondot.domain.model.request.OnboardingRequest
import com.ondot.domain.model.request.settings.home_address.HomeAddressRequest
import com.ondot.domain.model.request.settings.preparation_time.PreparationTimeRequest
import com.ondot.domain.model.member.HomeAddressInfo
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<AuthTokens>>
    suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>>
    suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>>
    suspend fun updateMapProvider(request: MapProviderRequest): Flow<Result<Unit>>
    suspend fun updateHomeAddress(request: HomeAddressRequest): Flow<Result<Unit>>
    suspend fun updatePreparationTime(request: PreparationTimeRequest): Flow<Result<Unit>>
    suspend fun setLocalMapProvider(mapProvider: MapProvider)
    fun getLocalMapProvider(): Flow<MapProvider>
    fun needsChooseProvider(): Flow<Boolean>
}