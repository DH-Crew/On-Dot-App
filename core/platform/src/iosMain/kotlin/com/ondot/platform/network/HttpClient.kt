package com.ondot.platform.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class, ExperimentalSerializationApi::class)
actual fun httpClient(): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient       = true
                encodeDefaults  = true
            }
        )
    }

    val prettyJson = Json {
        prettyPrint       = true
        prettyPrintIndent = "  "
        ignoreUnknownKeys = true
        isLenient         = true
        encodeDefaults    = true
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) = println("Ktor ▶ $message")
        }
        level = if (Platform.isDebugBinary) LogLevel.ALL else LogLevel.NONE
    }

    // 응답 바디가 JSON이면 예쁘게 출력
    install(ResponseObserver) {
        onResponse { response ->
            if (!Platform.isDebugBinary) return@onResponse

            val contentType = response.headers[HttpHeaders.ContentType] ?: ""
            if (contentType.startsWith("application/json")) {
                val text = response.bodyAsText()
                try {
                    val element = prettyJson.parseToJsonElement(text)
                    println("Ktor ▶ RESPONSE BODY\n${prettyJson.encodeToString(element)}")
                } catch (_: Exception) {
                    println("Ktor ▶ RESPONSE BODY\n$text")
                }
            }
        }
    }

    defaultRequest {
        header("Content-Type", "application/json")
        header("X-Mobile-Type", "IOS")
        header("X-Api-Version", "V1")
    }
}