package com.dh.ondot.network

import com.dh.ondot.data.model.TokenModel
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

actual class TokenProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual suspend fun getToken(): TokenModel? {
        val accessToken = userDefaults.stringForKey(ACCESS_TOKEN_KEY)
        val refreshToken = userDefaults.stringForKey(REFRESH_TOKEN_KEY)

        return if (accessToken != null && refreshToken != null) {
            TokenModel(accessToken = accessToken, refreshToken = refreshToken)
        } else {
            null
        }
    }

    actual suspend fun saveToken(newToken: TokenModel) {
        userDefaults.setValue(newToken.accessToken, forKey = ACCESS_TOKEN_KEY)
        userDefaults.setValue(newToken.refreshToken, forKey = REFRESH_TOKEN_KEY)
    }

    actual suspend fun clearToken() {
        userDefaults.removeObjectForKey(ACCESS_TOKEN_KEY)
        userDefaults.removeObjectForKey(REFRESH_TOKEN_KEY)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}