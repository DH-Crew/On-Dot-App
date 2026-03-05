package com.ondot.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class EverytimeValidateRequest(
    val everytimeUrl: String,
)
