package com.dh.ondot.core.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dh.ondot.data.dataStore
import com.dh.ondot.domain.model.ui.AlarmRingInfo
import com.dh.ondot.domain.service.AlarmStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AndroidAlarmStorage(
    private val context: Context
) : AlarmStorage {

    private val dataStore = context.dataStore

    private val ALARMS_KEY = stringPreferencesKey("alarm_list")

    override val alarmsFlow: Flow<List<AlarmRingInfo>> = dataStore.data
        .map { prefs ->
            prefs[ALARMS_KEY]?.let { json ->
                Json.decodeFromString<List<AlarmRingInfo>>(json)
            } ?: emptyList()
        }

    override suspend fun saveAlarms(alarms: List<AlarmRingInfo>) {
        val json = Json.encodeToString(alarms)
        dataStore.edit { prefs ->
            prefs[ALARMS_KEY] = json
        }
    }

    override suspend fun addAlarm(alarm: AlarmRingInfo) {
        dataStore.edit { prefs ->
            val currentJson = prefs[ALARMS_KEY]
            val currentList = currentJson
                ?.let { Json.decodeFromString<List<AlarmRingInfo>>(it) }
                ?.toMutableList()
                ?: mutableListOf()

            currentList.add(alarm)

            prefs[ALARMS_KEY] = Json.encodeToString(currentList)
        }
    }
}