package com.ondot.data.repository

import com.ondot.data.mapper.AuthResponseMapper
import com.ondot.domain.model.auth.AuthResult
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.repository.AuthRepository
import com.ondot.domain.service.TokenProvider
import com.ondot.network.HttpMethod
import com.ondot.network.NetworkClient
import com.ondot.network.base.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    networkClient: NetworkClient,
    private val tokenProvider: TokenProvider
): AuthRepository, BaseRepository(networkClient) {
    override suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResult>> = flow {
        emit(
            fetchMapped(
                method = HttpMethod.POST,
                path = "/auth/login/oauth",
                query = mapOf(
                    "provider" to provider,
                    "access_token" to accessToken
                ),
                mapper = AuthResponseMapper
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