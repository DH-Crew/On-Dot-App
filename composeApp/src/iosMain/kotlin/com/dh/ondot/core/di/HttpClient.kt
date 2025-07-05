package com.dh.ondot.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun httpClient(): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient       = true
            }
        )
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = println("Ktor ▶ $message")
        }
        level = if (Platform.isDebugBinary) LogLevel.ALL else LogLevel.NONE
    }

    defaultRequest { header("Content-Type", "application/json") }
}