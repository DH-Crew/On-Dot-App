package com.dh.ondot.core.network

import com.dh.ondot.core.di.httpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody

class NetworkClient(
    val tokenProvider: TokenProvider
) {
    val httpClient = httpClient()

    suspend inline fun <reified T> request(
        method: HttpMethod,
        path: String,
        queryParams: Map<String, Any> = emptyMap(),
        body: Any? = null,
        addAuthHeader: Boolean = true,
        isReissue: Boolean = false
    ): Result<T> {
        return try {
            val response = httpClient.request("$BASE_URL$path") {
                this.method = when (method) {
                    HttpMethod.GET -> io.ktor.http.HttpMethod.Get
                    HttpMethod.POST -> io.ktor.http.HttpMethod.Post
                    HttpMethod.PUT -> io.ktor.http.HttpMethod.Put
                    HttpMethod.PATCH -> io.ktor.http.HttpMethod.Patch
                    HttpMethod.DELETE -> io.ktor.http.HttpMethod.Delete
                }

                queryParams.forEach { (key, value) ->
                    url.parameters.append(key, value.toString())
                }

                if (body != null) {
                    setBody(body)
                }

                if (addAuthHeader) {
                    tokenProvider.getToken()?.let { token ->
                        header("Authorization", "Bearer ${if (isReissue) token.refreshToken else token.accessToken}")
                    }
                }
            }

            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}