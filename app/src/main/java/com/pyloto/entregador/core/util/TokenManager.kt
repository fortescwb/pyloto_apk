package com.pyloto.entregador.core.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de tokens JWT com DataStore.
 * Thread-safe e persistente. Suporta refresh automático.
 */
@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey(Constants.PREF_ACCESS_TOKEN)
        private val REFRESH_TOKEN_KEY = stringPreferencesKey(Constants.PREF_REFRESH_TOKEN)
        private val USER_ID_KEY = stringPreferencesKey(Constants.PREF_USER_ID)
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getAccessToken(): String? = runBlocking {
        dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()
    }

    fun getRefreshToken(): String? = runBlocking {
        dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()
    }

    suspend fun getUserId(): String? {
        return dataStore.data.map { it[USER_ID_KEY] }.first()
    }

    /**
     * Refresh síncrono do token (usado no interceptor).
     * TODO: Implementar chamada real ao endpoint de refresh.
     */
    fun refreshTokenSync(): String? {
        // Placeholder: implementar com chamada síncrona ao backend
        return null
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
