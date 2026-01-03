package com.karetlabs.erastos.network.util

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()

    data class Failure(
        val title: String,
        val message: String,
        val code: Int? = null
    ) : DataResult<Nothing>()
}