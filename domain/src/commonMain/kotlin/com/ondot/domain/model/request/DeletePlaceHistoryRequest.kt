package com.ondot.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class DeletePlaceHistoryRequest(
    val searchedAt: String = ""
)
