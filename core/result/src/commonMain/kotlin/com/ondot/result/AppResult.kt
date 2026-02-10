package com.ondot.result

sealed interface AppResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : AppResult<T>

    data class Error(
        val error: AppError,
    ) : AppResult<Nothing>
}
