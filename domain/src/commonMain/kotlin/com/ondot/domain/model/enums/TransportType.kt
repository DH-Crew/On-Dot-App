package com.ondot.domain.model.enums

enum class TransportType {
    PUBLIC_TRANSPORT,
    CAR,

    ;

    companion object {
        fun from(value: String?): TransportType =
            value
                ?.let {
                    runCatching { valueOf(it.trim().uppercase()) }.getOrNull()
                } ?: PUBLIC_TRANSPORT
    }
}
