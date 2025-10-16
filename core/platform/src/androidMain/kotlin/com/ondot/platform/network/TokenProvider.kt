package com.ondot.platform.network

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.platform.data.OnDotDataStore
import kotlinx.coroutines.flow.first

actual class TokenProvider(
    private val dataStore: OnDotDataStore
) {
    actual suspend fun getToken(): AuthTokens? {
        val accessToken = dataStore.accessToken.first()
        val refreshToken = dataStore.refreshToken.first()

        return if (accessToken != null && refreshToken != null) {
            AuthTokens(accessToken, refreshToken)
        } else {
            null
        }
    }

    actual suspend fun saveToken(newToken: AuthTokens) {
        dataStore.saveAccessToken(newToken.accessToken)
        dataStore.saveRefreshToken(newToken.refreshToken)
    }

    actual suspend fun clearToken() {
        dataStore.clearTokens()
    }
}