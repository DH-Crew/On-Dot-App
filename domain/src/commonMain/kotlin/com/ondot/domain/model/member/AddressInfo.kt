package com.ondot.domain.model.member
import kotlinx.serialization.Serializable

@Serializable
data class AddressInfo(
    val title: String = "",
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0
)
