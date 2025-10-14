package com.dh.ondot.core.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dh.ondot.data.dataStore
import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.service.MapProviderStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AndroidMapProviderStorage(
    private val context: Context
): MapProviderStorage {

    private val dataStore = context.dataStore

    override fun getMapProvider(): Flow<MapProvider> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                val raw = prefs[KEY]
                runCatching { raw?.let { enumValueOf<MapProvider>(it) } }
                    .getOrNull() ?: MapProvider.KAKAO
            }
            .distinctUntilChanged()
    }

    override fun needsChooseProvider(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[KEY_CONFIRM] != true
        }
    }

    override suspend fun setMapProvider(mapProvider: MapProvider) {
        dataStore.edit { prefs ->
            prefs[KEY] = mapProvider.name
            prefs[KEY_CONFIRM] = true
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(KEY)
            prefs.remove(KEY_CONFIRM)
        }
    }

    private companion object {
        val KEY = stringPreferencesKey("map_provider")
        val KEY_CONFIRM = booleanPreferencesKey("map_provider_confirm")
    }
}