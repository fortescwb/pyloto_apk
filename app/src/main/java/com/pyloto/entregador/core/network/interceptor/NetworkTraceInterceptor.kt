package com.pyloto.entregador.core.network.interceptor

import com.pyloto.entregador.BuildConfig
import com.pyloto.entregador.core.observability.NetworkDiagnostics
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkTraceInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val traceId = UUID.randomUUID().toString().replace("-", "").take(12)
        val method = originalRequest.method
        val path = originalRequest.url.encodedPath
        val startNs = System.nanoTime()

        val tracedRequest = originalRequest.newBuilder()
            .header(HEADER_TRACE_ID, traceId)
            .build()

        if (BuildConfig.DEBUG) {
            NetworkDiagnostics.logRequestStart(
                traceId = traceId,
                method = method,
                path = path
            )
        }

        return try {
            val response = chain.proceed(tracedRequest)
            val tookMs = (System.nanoTime() - startNs) / 1_000_000
            NetworkDiagnostics.logRequestEnd(
                traceId = traceId,
                method = method,
                path = path,
                code = response.code,
                tookMs = tookMs
            )
            response
        } catch (error: IOException) {
            val tookMs = (System.nanoTime() - startNs) / 1_000_000
            NetworkDiagnostics.logRequestFailure(
                traceId = traceId,
                method = method,
                path = path,
                error = error,
                tookMs = tookMs
            )
            throw error
        }
    }

    private companion object {
        const val HEADER_TRACE_ID = "X-Trace-Id"
    }
}

