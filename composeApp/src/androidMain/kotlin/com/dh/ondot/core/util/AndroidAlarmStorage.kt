package com.dh.ondot.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dh.ondot.data.dataStore
import com.dh.ondot.domain.service.AlarmStorage
import com.dh.ondot.domain.model.response.AlarmDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AndroidAlarmStorage(
    private val context: Context
) : AlarmStorage {

    private val dataStore = context.dataStore

    private val ALARMS_KEY = stringPreferencesKey("alarm_list")

    override val alarmsFlow: Flow<List<AlarmDetail>> = dataStore.data
        .map { prefs ->
            prefs[ALARMS_KEY]?.let { json ->
                Json.decodeFromString<List<AlarmDetail>>(json)
            } ?: emptyList()
        }

    override suspend fun saveAlarms(alarms: List<AlarmDetail>) {
        val json = Json.encodeToString(alarms)
        dataStore.edit { prefs ->
            prefs[ALARMS_KEY] = json
        }
    }
}