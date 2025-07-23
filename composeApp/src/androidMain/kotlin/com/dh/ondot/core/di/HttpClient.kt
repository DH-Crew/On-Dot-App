package com.dh.ondot.core.di

import com.dh.ondot.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
actual fun httpClient(): HttpClient = HttpClient(OkHttp) {
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
        prettyPrint        = true
        prettyPrintIndent  = "  "
        ignoreUnknownKeys  = true
        isLenient          = true
        encodeDefaults     = true
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level  = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.NONE
    }

    install(ResponseObserver) {
        onResponse { response ->
            if (response.status.value in 200..299 &&
                response.headers[HttpHeaders.ContentType]?.startsWith("application/json") == true) {
                val text = response.bodyAsText()

                try {
                    val element = prettyJson.parseToJsonElement(text)
                    println("Ktor ▶\n${prettyJson.encodeToString(element)}")
                } catch (_: Exception) {
                    println("Ktor ▶\n$text")
                }
            }
        }
    }

    defaultRequest { header("Content-Type", "application/json") }
}