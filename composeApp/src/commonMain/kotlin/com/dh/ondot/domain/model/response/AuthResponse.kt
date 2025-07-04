package com.dh.ondot.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String = "",
    val refreshToken: String = "",
    val isNewMember: Boolean = false
)
