package com.ondot.data.model.response.member

import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    val title: String = "",
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
)
