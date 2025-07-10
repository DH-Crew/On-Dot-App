package com.dh.ondot.data.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.request.OnboardingRequest
import com.dh.ondot.domain.model.response.HomeAddressInfo
import com.dh.ondot.domain.repository.MemberRepository
import com.dh.ondot.network.HttpMethod
import com.dh.ondot.network.NetworkClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MemberRepositoryImpl(
    private val networkClient: NetworkClient
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
}