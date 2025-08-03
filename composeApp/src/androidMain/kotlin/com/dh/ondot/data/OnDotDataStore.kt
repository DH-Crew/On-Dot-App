package com.dh.ondot.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "ondot_prefs")

class OnDotDataStore(context: Context) {
    private val dataStore = context.dataStore
    private val logger = Logger.withTag("OnDotDataStore")

    val accessToken: Flow<String?> = dataStore.data.map { preferences -> preferences[ACCESS_TOKEN_KEY] }
    val refreshToken: Flow<String?> = dataStore.data.map { preferences -> preferences[REFRESH_TOKEN_KEY] }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
            logger.i { "Access Token: $token" }
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
            logger.i { "Refresh Token: $token" }
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            logger.i { "Access/Refresh Token cleared" }
        }
    }

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}