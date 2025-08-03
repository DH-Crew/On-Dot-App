package com.dh.ondot.network

import android.content.Context
import com.dh.ondot.data.OnDotDataStore
import com.dh.ondot.data.model.TokenModel
import kotlinx.coroutines.flow.first

actual class TokenProvider(private val context: Context) {
    private val dataStore = OnDotDataStore(context)

    actual suspend fun getToken(): TokenModel? {
        val accessToken = dataStore.accessToken.first()
        val refreshToken = dataStore.refreshToken.first()

        return if (accessToken != null && refreshToken != null) {
            TokenModel(accessToken, refreshToken)
        } else {
            null
        }
    }

    actual suspend fun saveToken(newToken: TokenModel) {
        dataStore.saveAccessToken(newToken.accessToken)
        dataStore.saveRefreshToken(newToken.refreshToken)
    }

    actual suspend fun clearToken() {
        dataStore.clearTokens()
    }
}