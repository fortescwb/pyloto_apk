package com.pyloto.entregador.core.observability

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.pyloto.entregador.BuildConfig
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkDiagnostics {
    private const val TAG_HTTP = "PylotoHttp"
    private const val TAG_REPO = "PylotoRepo"

    fun logRequestStart(traceId: String, method: String, path: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG_HTTP, "[$traceId] --> $method $path")
        }
    }

    fun logRequestEnd(traceId: String, method: String, path: String, code: Int, tookMs: Long) {
        val message = "[$traceId] <-- $code $method $path (${tookMs}ms)"
        when {
            code >= 500 -> Log.e(TAG_HTTP, message)
            code >= 400 -> Log.w(TAG_HTTP, message)
            BuildConfig.DEBUG -> Log.d(TAG_HTTP, message)
        }
    }

    fun logRequestFailure(
        traceId: String,
        method: String,
        path: String,
        error: Throwable,
        tookMs: Long
    ) {
        val reason = classifyError(error)
        val message = "[$traceId] xx $method $path (${tookMs}ms) reason=$reason"
        Log.w(TAG_HTTP, message, error)
    }

    fun logRepositoryFallback(operation: String, fallback: String, error: Throwable) {
        val reason = classifyError(error)
        val message = "operation=$operation fallback=$fallback reason=$reason"
        Log.w(TAG_REPO, message, error)
    }

    fun logParsingFailure(operation: String, error: Throwable) {
        val message = "operation=$operation reason=parsing_error"
        Log.e(TAG_REPO, message, error)
    }

    private fun classifyError(error: Throwable): String {
        return when (error) {
            is UnknownHostException -> "dns_or_offline"
            is ConnectException -> "connection_refused"
            is SocketTimeoutException -> "timeout"
            is JsonSyntaxException, is MalformedJsonException -> "parsing_error"
            else -> error::class.simpleName ?: "unknown"
        }
    }
}

