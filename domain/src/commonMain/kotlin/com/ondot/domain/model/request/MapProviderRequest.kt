package com.dh.ondot.domain.model.request

import com.dh.ondot.domain.model.enums.MapProvider
import kotlinx.serialization.Serializable

@Serializable
data class MapProviderRequest(
    val mapProvider: MapProvider
)
