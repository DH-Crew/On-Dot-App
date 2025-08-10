package com.dh.ondot.core.util

import com.dh.ondot.domain.model.enums.MapProvider
import com.dh.ondot.domain.service.MapProviderStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDefaultsDidChangeNotification

class IosMapProviderStorage: MapProviderStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val key = "map_provider"
    private val flagKey = "map_provider_confirm"

    private val state = MutableStateFlow(load())
    private val needsState = MutableStateFlow(loadNeedsChoose())

    init {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = defaults,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            state.value = load()
            needsState.value = loadNeedsChoose()
        }
    }

    override fun getMapProvider(): Flow<MapProvider> = state

    override fun needsChooseProvider(): Flow<Boolean> = needsState

    override suspend fun setMapProvider(mapProvider: MapProvider) {
        defaults.setObject(mapProvider.name, forKey = key)
        defaults.synchronize() // 즉시 디스크 반영
        state.value = mapProvider
    }

    override suspend fun clear() {
        defaults.removeObjectForKey(key)
        defaults.removeObjectForKey(flagKey)
        defaults.synchronize()
        state.value = MapProvider.KAKAO
        needsState.value = true
    }

    private fun load(): MapProvider =
        runCatching {
            defaults.stringForKey(key)?.let { enumValueOf<MapProvider>(it) }
        }.getOrNull() ?: MapProvider.KAKAO

    private fun loadNeedsChoose(): Boolean {
        val confirmed = defaults.boolForKey(flagKey)
        return !confirmed
    }
}