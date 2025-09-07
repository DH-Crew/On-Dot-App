package com.dh.ondot.core.network

abstract class BaseRepository(
    protected val network: NetworkClient
) {
    protected suspend inline fun <reified T> fetch(
        method: HttpMethod,
        path: String,
        body: Any? = null,
        query: Map<String, String> = emptyMap(),
        addAuthHeader: Boolean = true,
        retries: Int = 0,
        isReissue: Boolean = false
    ): Result<T> {
        val client = network

        return retryResult(retries = retries) {
            try {
                client.request<T>(
                    path = path,
                    method = method,
                    body = body,
                    queryParams = query,
                    addAuthHeader = addAuthHeader,
                    isReissue = isReissue
                )
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }
}