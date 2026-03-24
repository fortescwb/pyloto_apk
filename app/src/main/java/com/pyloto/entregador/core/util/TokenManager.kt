package com.pyloto.entregador.core.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey(Constants.PREF_ACCESS_TOKEN)
        val REFRESH_TOKEN_KEY = stringPreferencesKey(Constants.PREF_REFRESH_TOKEN)
        val USER_ID_KEY = stringPreferencesKey(Constants.PREF_USER_ID)
        val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey(Constants.PREF_ONBOARDING_COMPLETE)
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

    suspend fun saveOnboardingComplete(isComplete: Boolean) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE_KEY] = isComplete
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

    suspend fun getOnboardingComplete(): Boolean? {
        return dataStore.data.map { it[ONBOARDING_COMPLETE_KEY] }.first()
    }

    /**
     * Refresh sincrono no interceptor nao esta habilitado neste ciclo.
     * O refresh assinado continua via fluxo de auth da aplicacao.
     */
    fun refreshTokenSync(): String? {
        return null
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
            prefs.remove(ONBOARDING_COMPLETE_KEY)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
