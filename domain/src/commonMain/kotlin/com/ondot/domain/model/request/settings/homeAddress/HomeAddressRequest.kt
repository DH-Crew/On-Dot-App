package com.ondot.domain.model.request.settings.homeAddress

import kotlinx.serialization.Serializable

@Serializable
data class HomeAddressRequest(
    val roadAddress: String,
    val latitude: Double,
    val longitude: Double,
)
