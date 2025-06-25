package com.dh.ondot.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class NetworkClient(
    val tokenProvider: TokenProvider
) {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Logging) {
            level = LogLevel.INFO
        }

        defaultRequest {
            header("Content-Type", "application/json")
        }
    }

    suspend inline fun <reified T> request(
        method: HttpMethod,
        body: Any? = null,
        addAuthHeader: Boolean = true
    ): Result<T> {
        return try {
            val response = httpClient.request(BASE_URL) {
                this.method = when (method) {
                    HttpMethod.GET -> io.ktor.http.HttpMethod.Get
                    HttpMethod.POST -> io.ktor.http.HttpMethod.Post
                    HttpMethod.PUT -> io.ktor.http.HttpMethod.Put
                    HttpMethod.PATCH -> io.ktor.http.HttpMethod.Patch
                    HttpMethod.DELETE -> io.ktor.http.HttpMethod.Delete
                }

                if (body != null) {
                    setBody(body)
                }

                if (addAuthHeader) {
                    tokenProvider.getToken()?.let { token ->
                        header("Authorization", "Bearer ${token.accessToken}")
                    }
                }
            }

            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}