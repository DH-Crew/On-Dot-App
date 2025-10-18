package com.ondot.data.model.response.member

import kotlinx.serialization.Serializable

@Serializable
data class HomeAddressResponse(
    val roadAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)