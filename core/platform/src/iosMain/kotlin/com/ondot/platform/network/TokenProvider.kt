package com.ondot.platform.network

import com.ondot.domain.model.auth.AuthTokens
import com.ondot.domain.service.TokenProvider
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class IosTokenProvider: TokenProvider {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun getToken(): AuthTokens? {
        val accessToken = userDefaults.stringForKey(ACCESS_TOKEN_KEY)
        val refreshToken = userDefaults.stringForKey(REFRESH_TOKEN_KEY)

        return if (accessToken != null && refreshToken != null) {
            AuthTokens(accessToken = accessToken, refreshToken = refreshToken)
        } else {
            null
        }
    }

    override suspend fun saveToken(newToken: AuthTokens) {
        userDefaults.setValue(newToken.accessToken, forKey = ACCESS_TOKEN_KEY)
        userDefaults.setValue(newToken.refreshToken, forKey = REFRESH_TOKEN_KEY)
    }

    override suspend fun clearToken() {
        userDefaults.removeObjectForKey(ACCESS_TOKEN_KEY)
        userDefaults.removeObjectForKey(REFRESH_TOKEN_KEY)
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}