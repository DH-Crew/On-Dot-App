package com.ondot.data.model.response.member

import kotlinx.serialization.Serializable

@Serializable
data class PreparationTimeResponse(
    val preparationTime: Int,
    val updatedAt: String
)
