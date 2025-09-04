package com.dh.ondot.domain.model.request.settings.preparation_time

import kotlinx.serialization.Serializable

@Serializable
data class PreparationTimeRequest(
    val preparationTime: Int
)
