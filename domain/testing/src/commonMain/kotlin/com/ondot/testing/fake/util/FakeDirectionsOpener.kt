package com.ondot.testing.fake.util

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.service.DirectionsOpener

class FakeDirectionsOpener: DirectionsOpener {
    data class Call(
        val startLat: Double,
        val startLng: Double,
        val endLat: Double,
        val endLng: Double,
        val provider: MapProvider
    )

    val calls = mutableListOf<Call>()

    override fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String
    ) {
        calls += Call(
            startLat = startLat,
            startLng = startLng,
            endLat = endLat,
            endLng = endLng,
            provider = provider
        )
    }
}