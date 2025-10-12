package com.ondot.domain.repository

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.model.response.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResponse>>
    suspend fun logout(): Flow<Result<Unit>>
    suspend fun saveToken(token: AuthTokens)
    suspend fun reissueToken(): Flow<Result<AuthTokens>>
}