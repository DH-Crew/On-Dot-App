package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class HomeAddressInfo(
    val roadAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
