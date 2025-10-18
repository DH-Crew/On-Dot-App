package com.ondot.domain.repository

import com.ondot.domain.model.auth.AuthResult
import com.ondot.domain.model.auth.AuthTokens
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResult>>
    suspend fun logout(): Flow<Result<Unit>>
    suspend fun saveToken(token: AuthTokens)
    suspend fun reissueToken(): Flow<Result<AuthTokens>>
}