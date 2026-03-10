package com.ondot.data.model.request.everytime

import kotlinx.serialization.Serializable

@Serializable
data class EverytimeValidateRequest(
    val everytimeUrl: String,
)
