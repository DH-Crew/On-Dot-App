package com.ondot.domain.service

import com.ondot.domain.model.auth.AuthTokens

interface TokenProvider {
    suspend fun getToken(): AuthTokens?
    suspend fun saveToken(newToken: AuthTokens)
    suspend fun clearToken()
}