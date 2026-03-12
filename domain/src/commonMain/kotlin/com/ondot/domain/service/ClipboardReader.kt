package com.ondot.domain.service

interface ClipboardReader {
    suspend fun readText(): String?
}
