package com.dh.ondot.domain.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.request.OnboardingRequest
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun completeOnboarding(request: OnboardingRequest): Flow<Result<TokenModel>>
}