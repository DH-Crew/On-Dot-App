package com.ondot.data.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val memberId: Long = -1,
    val accessToken: String = "",
    val refreshToken: String = "",
    val isNewMember: Boolean = false
)
