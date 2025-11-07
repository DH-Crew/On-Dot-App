package com.ondot.domain.model.auth

data class AuthResult(
    val memberId: Long = -1,
    val tokens: AuthTokens = AuthTokens(),
    val isNewMember: Boolean = false
)
