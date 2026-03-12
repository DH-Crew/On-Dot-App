package com.ondot.platform.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ondot.domain.model.ui.AlarmRingInfo
import com.ondot.domain.service.AlarmStorage
import com.ondot.platform.data.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class AndroidAlarmStorage(
    private val context: Context,
) : AlarmStorage {
    private val dataStore = context.dataStore

    private val alarmsKey = stringPreferencesKey("alarm_list")

    override val alarmsFlow: Flow<List<AlarmRingInfo>> =
        dataStore.data
            .map { prefs ->
                prefs[alarmsKey]?.let { json ->
                    Json.decodeFromString<List<AlarmRingInfo>>(json)
                } ?: emptyList()
            }

    override suspend fun saveAlarms(alarms: List<AlarmRingInfo>) {
        val json = Json.encodeToString(alarms)
        dataStore.edit { prefs ->
            prefs[alarmsKey] = json
        }
    }

    override suspend fun addAlarm(alarm: AlarmRingInfo) {
        dataStore.edit { prefs ->
            val currentJson = prefs[alarmsKey]
            val currentList =
                currentJson
                    ?.let { Json.decodeFromString<List<AlarmRingInfo>>(it) }
                    ?.toMutableList()
                    ?: mutableListOf()

            currentList.add(alarm)

            prefs[alarmsKey] = Json.encodeToString(currentList)
        }
    }
}
