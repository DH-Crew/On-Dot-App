package com.ondot.domain.service

import com.ondot.domain.model.enums.MapProvider

interface DirectionsOpener {
    fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String
    )
}