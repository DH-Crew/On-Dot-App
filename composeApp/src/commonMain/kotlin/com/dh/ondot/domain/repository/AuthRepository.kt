package com.dh.ondot.domain.repository

import com.dh.ondot.data.model.TokenModel
import com.dh.ondot.domain.model.response.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(provider: String, accessToken: String): Flow<Result<AuthResponse>>
    suspend fun saveToken(token: TokenModel)
}