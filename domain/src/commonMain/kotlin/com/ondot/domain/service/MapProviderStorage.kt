package com.ondot.domain.service

import com.ondot.domain.model.enums.MapProvider
import kotlinx.coroutines.flow.Flow

interface MapProviderStorage {
    fun getMapProvider(): Flow<MapProvider>
    fun needsChooseProvider(): Flow<Boolean>
    suspend fun clear()
    suspend fun setMapProvider(mapProvider: MapProvider)
}