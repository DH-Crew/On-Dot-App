package com.ondot.domain.model.auth

data class AuthResult(
    val tokens: AuthTokens = AuthTokens(),
    val isNewMember: Boolean = false
)
