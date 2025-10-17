package com.ondot.domain.model.request

import com.ondot.domain.model.enums.MapProvider
import kotlinx.serialization.Serializable

@Serializable
data class MapProviderRequest(
    val mapProvider: MapProvider
)
