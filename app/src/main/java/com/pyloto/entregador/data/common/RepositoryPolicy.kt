package com.pyloto.entregador.data.common

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.pyloto.entregador.core.observability.NetworkDiagnostics

suspend inline fun <T> withCacheFallback(
    operation: String = "unknown_operation",
    crossinline remote: suspend () -> T,
    crossinline local: suspend () -> T
): T {
    return try {
        remote()
    } catch (error: Exception) {
        NetworkDiagnostics.logRepositoryFallback(
            operation = operation,
            fallback = "cache",
            error = error
        )
        if (error is JsonSyntaxException || error is MalformedJsonException) {
            NetworkDiagnostics.logParsingFailure(operation = operation, error = error)
        }
        local()
    }
}

suspend inline fun <T> withNetworkGuard(
    operation: String = "unknown_operation",
    isConnected: Boolean,
    crossinline remote: suspend () -> T,
    crossinline local: suspend () -> T
): T {
    return if (isConnected) {
        withCacheFallback(
            operation = operation,
            remote = remote,
            local = local
        )
    } else {
        local()
    }
}

suspend inline fun <T> withFallbackValue(
    operation: String = "unknown_operation",
    fallbackValue: T,
    crossinline call: suspend () -> T
): T {
    return try {
        call()
    } catch (error: Exception) {
        NetworkDiagnostics.logRepositoryFallback(
            operation = operation,
            fallback = "default_value",
            error = error
        )
        if (error is JsonSyntaxException || error is MalformedJsonException) {
            NetworkDiagnostics.logParsingFailure(operation = operation, error = error)
        }
        fallbackValue
    }
}
