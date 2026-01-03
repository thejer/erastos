package com.karetlabs.erastos.network.util

class RemoteDataSourceException(
    override val message: String,
    val kind: RemoteErrorKind,
    val httpCode: Int? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

enum class RemoteErrorKind {
    Http,
    Network,
    Unknown
}