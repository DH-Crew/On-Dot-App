package com.ondot.network.execute

import com.ondot.network.model.ErrorResponse
import com.ondot.result.AppError
import com.ondot.result.AppResult
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

val errorJson =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

suspend inline fun <T> safeApiCall(crossinline call: suspend () -> T): AppResult<T> =
    try {
        AppResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val status = e.response.status.value
        val raw = runCatching { e.response.bodyAsText() }.getOrNull()
        val parsed =
            runCatching {
                raw?.let { errorJson.decodeFromString<ErrorResponse>(it) }
            }.getOrNull()

        AppResult.Error(
            AppError.Http(
                status = status,
                errorCode = parsed?.errorCode,
                message = parsed?.message,
                rawBody = raw,
            ),
        )
    } catch (e: SocketTimeoutException) {
        AppResult.Error(AppError.Timeout(e))
    } catch (e: IOException) {
        AppResult.Error(AppError.Network(e))
    } catch (e: SerializationException) {
        AppResult.Error(AppError.Serialization(e))
    } catch (e: Throwable) {
        AppResult.Error(AppError.Unknown(e))
    }
