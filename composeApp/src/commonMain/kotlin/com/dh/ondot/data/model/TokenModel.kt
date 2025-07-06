package com.dh.ondot.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = ""
)
