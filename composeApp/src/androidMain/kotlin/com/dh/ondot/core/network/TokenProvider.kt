package com.dh.ondot.core.network

import android.content.Context
import com.dh.ondot.data.OnDotDataStore
import com.dh.ondot.data.model.AuthTokens
import kotlinx.coroutines.flow.first

actual class TokenProvider(private val context: Context) {
    private val dataStore = OnDotDataStore(context)

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