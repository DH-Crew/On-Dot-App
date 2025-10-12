package com.dh.ondot.core.network

import com.dh.ondot.data.model.AuthTokens

expect class TokenProvider {
    suspend fun getToken(): AuthTokens?
    suspend fun saveToken(newToken: AuthTokens)
    suspend fun clearToken()
}