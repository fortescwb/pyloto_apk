package com.pyloto.entregador.core.network.interceptor

import android.util.Log
import com.pyloto.entregador.core.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor para injetar o token JWT em todas as requisições.
 * Executa refresh automático quando o backend retorna 401.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private val NO_AUTH_ENDPOINTS = listOf(
            "auth/login",
            "auth/register",
            "auth/refresh"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val isPublicEndpoint = NO_AUTH_ENDPOINTS.any {
            originalRequest.url.encodedPath.contains(it)
        }

        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()
            ?.takeIf { it.isNotBlank() }
        val builder = originalRequest.newBuilder()
            .header("Accept", "application/json")
        if (token != null) {
            builder.header("Authorization", "Bearer $token")
        }
        val authenticatedRequest = builder.build()

        val response = chain.proceed(authenticatedRequest)

        if (response.code == 401) {
            response.close()
            Log.d(TAG, "401 recebido — tentando refresh do token")

            val newToken = tokenManager.refreshTokenSync()
            if (newToken != null) {
                val retryRequest = originalRequest.newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(retryRequest)
            }

            Log.w(TAG, "Refresh falhou — propagando 401")
        }

        return response
    }
}
