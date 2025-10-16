package com.ondot.network

import com.ondot.domain.model.auth.AuthTokens

expect class TokenProvider {
    suspend fun getToken(): AuthTokens?
    suspend fun saveToken(newToken: AuthTokens)
    suspend fun clearToken()
}