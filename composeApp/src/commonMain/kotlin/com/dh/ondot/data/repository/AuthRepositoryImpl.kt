package com.dh.ondot.data.repository

import com.dh.ondot.core.network.BaseRepository
import com.dh.ondot.core.network.HttpMethod
import com.dh.ondot.core.network.NetworkClient
import com.dh.ondot.core.network.TokenProvider
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.response.AuthResponse
import com.ondot.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    networkClient: NetworkClient,
    private val tokenProvider: TokenProvider
): AuthRepository, BaseRepository(networkClient) {
    override suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResponse>> = flow {
        emit(
            fetch(
                method = HttpMethod.POST,
                path = "/auth/login/oauth",
                query = mapOf(
                    "provider" to provider,
                    "access_token" to accessToken
                )
            )
        )
    }

    override suspend fun logout(): Flow<Result<Unit>> = flow {
        emit(fetch(HttpMethod.POST, "/auth/logout"))
    }

    override suspend fun saveToken(token: AuthTokens) {
        tokenProvider.saveToken(token)
    }

    override suspend fun reissueToken(): Flow<Result<AuthTokens>> = flow {
        emit(
            fetch(
                method = HttpMethod.POST,
                path = "/auth/reissue",
                isReissue = true
            )
        )
    }
}