package com.ondot.domain.model.request.settings.preparationTime

import kotlinx.serialization.Serializable

@Serializable
data class PreparationTimeRequest(
    val preparationTime: Int,
)
