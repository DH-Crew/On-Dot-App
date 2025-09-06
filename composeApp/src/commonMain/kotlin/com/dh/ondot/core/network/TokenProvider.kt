package com.dh.ondot.core.network

import com.dh.ondot.data.model.TokenModel

expect class TokenProvider {
    suspend fun getToken(): TokenModel?
    suspend fun saveToken(newToken: TokenModel)
    suspend fun clearToken()
}