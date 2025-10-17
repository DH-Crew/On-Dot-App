package com.ondot.domain.model.request
import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val withdrawalReasonId: Long,
    val customReason: String
)
