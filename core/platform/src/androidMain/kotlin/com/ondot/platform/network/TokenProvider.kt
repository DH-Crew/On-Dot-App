package com.ondot.platform.network

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.service.TokenProvider
import com.ondot.platform.data.OnDotDataStore
import kotlinx.coroutines.flow.first

class AndroidTokenProvider(
    private val dataStore: OnDotDataStore
) : TokenProvider {
    override suspend fun getToken(): AuthTokens? {
        val accessToken = dataStore.accessToken.first()
        val refreshToken = dataStore.refreshToken.first()

        return if (accessToken != null && refreshToken != null) {
            AuthTokens(accessToken, refreshToken)
        } else {
            null
        }
    }

    override suspend fun saveToken(newToken: AuthTokens) {
        dataStore.saveAccessToken(newToken.accessToken)
        dataStore.saveRefreshToken(newToken.refreshToken)
    }

    override suspend fun clearToken() {
        dataStore.clearTokens()
    }
}