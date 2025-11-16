package com.ondot.domain.model.member

data class PlaceHistory(
    val title: String = "",
    val roadAddress: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val searchedAt: String = ""
)