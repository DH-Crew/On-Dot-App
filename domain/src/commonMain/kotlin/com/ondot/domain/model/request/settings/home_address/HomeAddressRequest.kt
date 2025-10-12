package com.ondot.domain.model.request.settings.home_address

data class HomeAddressRequest(
    val roadAddress: String,
    val latitude: Double,
    val longitude: Double
)
