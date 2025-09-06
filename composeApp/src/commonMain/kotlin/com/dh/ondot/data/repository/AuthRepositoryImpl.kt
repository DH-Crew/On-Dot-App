package com.dh.ondot.data.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.response.AuthResponse
import com.dh.ondot.domain.repository.AuthRepository
import com.dh.ondot.core.network.HttpMethod
import com.dh.ondot.core.network.NetworkClient
import com.dh.ondot.core.network.TokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tokenProvider: TokenProvider
): AuthRepository {
    override suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResponse>> = flow {
        val response = networkClient.request<AuthResponse>(
            method = HttpMethod.POST,
            path = "/auth/login/oauth",
            queryParams = mapOf("provider" to provider, "access_token" to accessToken)
        )

        emit(response.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        ))
    }

    override suspend fun logout(): Flow<Result<Unit>> = flow {
        val response = networkClient.request<Unit>(
            method = HttpMethod.POST,
            path = "/auth/logout"
        )

        emit(response.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        ))
    }

    override suspend fun saveToken(token: TokenModel) {
        tokenProvider.saveToken(token)
    }

    override suspend fun reissueToken(): Flow<Result<TokenModel>> = flow {
        val response = networkClient.request<TokenModel>(
            method = HttpMethod.POST,
            path = "/auth/reissue",
            isReissue = true
        )

        emit(response.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        ))
    }
}