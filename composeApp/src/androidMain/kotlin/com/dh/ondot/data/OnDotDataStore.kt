package com.dh.ondot.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.touchlab.kermit.Logger
import com.dh.ondot.domain.RingingState
import com.dh.ondot.domain.model.enums.AlarmType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "ondot_prefs")

class OnDotDataStore(context: Context) {
    private val dataStore = context.applicationContext.dataStore
    private val logger = Logger.withTag("OnDotDataStore")

    /**--------------------------------------------JwtToken-----------------------------------------------*/

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

    /**--------------------------------------------RingingState-----------------------------------------------*/

    suspend fun setRinging(scheduleId: Long, alarmId: Long, type: AlarmType) {
        dataStore.edit {
            it[KEY_IS_RINGING]  = true
            it[KEY_SCHEDULE_ID] = scheduleId
            it[KEY_ALARM_ID]    = alarmId
            it[KEY_TYPE]        = type.name
        }
    }

    suspend fun clearRinging() {
        dataStore.edit {
            it[KEY_IS_RINGING]  = false
            it.remove(KEY_SCHEDULE_ID)
            it.remove(KEY_ALARM_ID)
            it.remove(KEY_TYPE)
        }
    }

    fun flow() = dataStore.data.map { p ->
        val on = p[KEY_IS_RINGING] == true
        val sid = p[KEY_SCHEDULE_ID] ?: -1L
        val aid = p[KEY_ALARM_ID] ?: -1L
        val t = runCatching { AlarmType.valueOf(p[KEY_TYPE].orEmpty()) }.getOrDefault(AlarmType.Departure)
        RingingState(on, sid, aid, t)
    }

    suspend fun currentRinging(): RingingState = flow().first()

    /**--------------------------------------------DataStoreKey-----------------------------------------------*/

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val KEY_IS_RINGING   = booleanPreferencesKey("is_ringing")
        val KEY_SCHEDULE_ID  = longPreferencesKey("schedule_id")
        val KEY_ALARM_ID     = longPreferencesKey("alarm_id")
        val KEY_TYPE         = stringPreferencesKey("type")
    }
}