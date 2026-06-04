package com.dh.ondot.core

import com.ondot.domain.model.enums.MapProvider
import com.ondot.domain.model.enums.TransportType
import com.ondot.domain.service.DirectionsOpener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DirectionsFacade : KoinComponent {
    private val opener: DirectionsOpener by inject()

    fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String,
    ) {
        openDirections(
            startLat = startLat,
            startLng = startLng,
            endLat = endLat,
            endLng = endLng,
            provider = provider,
            startName = startName,
            endName = endName,
            transportType = TransportType.PUBLIC_TRANSPORT,
        )
    }

    fun openDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        provider: MapProvider,
        startName: String,
        endName: String,
        transportType: TransportType,
    ) {
        opener.openDirections(startLat, startLng, endLat, endLng, provider, startName, endName, transportType)
    }
}
