package com.ondot.domain.model.request

data class DeleteAccountRequest(
    val withdrawalReasonId: Long,
    val customReason: String
)
