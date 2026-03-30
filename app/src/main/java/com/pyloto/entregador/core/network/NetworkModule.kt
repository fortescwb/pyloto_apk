package com.pyloto.entregador.core.network

import com.pyloto.entregador.core.network.interceptor.AuthInterceptor
import com.pyloto.entregador.core.network.interceptor.NetworkTraceInterceptor
import com.pyloto.entregador.core.util.TokenManager
import com.pyloto.entregador.BuildConfig
import com.pyloto.entregador.data.auth.remote.dto.RefreshTokenRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkTraceInterceptor: NetworkTraceInterceptor,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkTraceInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit, tokenManager: TokenManager): ApiService {
        val apiService = retrofit.create(ApiService::class.java)

        tokenManager.refreshExecutor = { refreshToken ->
            try {
                val call = retrofit.create(ApiService::class.java)
                val response = kotlinx.coroutines.runBlocking {
                    call.refreshToken(RefreshTokenRequest(refreshToken = refreshToken))
                }
                val authData = response.requireData()
                val newAccess = authData.accessToken.takeIf { it.isNotBlank() }
                if (newAccess != null) {
                    val newRefresh = authData.refreshToken?.takeIf { it.isNotBlank() }
                        ?: refreshToken
                    kotlinx.coroutines.runBlocking {
                        tokenManager.saveTokens(newAccess, newRefresh)
                    }
                }
                newAccess
            } catch (_: Exception) {
                null
            }
        }

        return apiService
    }
}
