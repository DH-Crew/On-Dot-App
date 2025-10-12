package com.ondot.domain.model.response

data class AuthResponse(
    val accessToken: String = "",
    val refreshToken: String = "",
    val isNewMember: Boolean = false
)
