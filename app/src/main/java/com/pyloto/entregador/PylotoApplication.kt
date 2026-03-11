package com.pyloto.entregador

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PylotoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeFirebaseIfConfigured()
    }

    private fun initializeFirebaseIfConfigured() {
        val options = FirebaseOptions.fromResource(this)
        if (options == null || options.apiKey.isNullOrBlank()) {
            Log.w(TAG, "Firebase desabilitado: configuração ausente.")
            return
        }

        if (isPlaceholderApiKey(options.apiKey)) {
            Log.w(TAG, "Firebase desabilitado: API key placeholder detectada.")
            return
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this, options)
            Log.i(TAG, "Firebase inicializado com sucesso.")
        }
    }

    private fun isPlaceholderApiKey(apiKey: String): Boolean {
        val normalized = apiKey.trim().lowercase()
        return normalized.contains("placeholder") ||
            normalized.contains("replace-with-real-key") ||
            normalized == "your_api_key"
    }

    companion object {
        private const val TAG = "PylotoApplication"
    }
}
