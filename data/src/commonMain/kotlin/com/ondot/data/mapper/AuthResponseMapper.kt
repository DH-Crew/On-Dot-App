package com.ondot.data.mapper

import com.ondot.data.model.response.auth.AuthResponse
import com.ondot.domain.model.auth.AuthResult
import com.ondot.domain.model.auth.AuthTokens
import com.ondot.network.base.Mapper

object AuthResponseMapper: Mapper<AuthResponse, AuthResult> {
    override fun responseToModel(response: AuthResponse?): AuthResult {
        return response?.let {
            AuthResult(
                tokens = AuthTokens(it.accessToken, it.refreshToken),
                isNewMember = it.isNewMember
            )
        } ?: AuthResult()
    }
}