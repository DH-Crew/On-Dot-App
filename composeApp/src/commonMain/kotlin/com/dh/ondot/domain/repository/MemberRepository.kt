package com.dh.ondot.domain.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.request.DeleteAccountRequest
import com.dh.ondot.domain.model.request.MapProviderRequest
import com.dh.ondot.domain.model.request.OnboardingRequest
import com.dh.ondot.domain.model.response.HomeAddressInfo
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<TokenModel>>
    suspend fun getHomeAddress(): Flow<Result<HomeAddressInfo>>
    suspend fun withdrawUser(request: DeleteAccountRequest): Flow<Result<Unit>>
    suspend fun updateMapProvider(request: MapProviderRequest): Flow<Result<Unit>>
}