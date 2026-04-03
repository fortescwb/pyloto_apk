package com.pyloto.entregador.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.provider.Settings
import android.util.Log
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pyloto.entregador.MainActivity
import com.pyloto.entregador.R
import com.pyloto.entregador.BuildConfig
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.util.Constants
import com.pyloto.entregador.data.notificacao.remote.dto.FCMTokenRequest
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PylotoFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            ?.takeIf { it.isNotBlank() }

        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            FirebaseServiceEntryPoint::class.java
        )

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                entryPoint.apiService().registrarTokenFCM(
                    FCMTokenRequest(
                        token = token,
                        deviceId = deviceId,
                        appVersion = BuildConfig.VERSION_NAME,
                        pushEnabled = true
                    )
                )
            }.onFailure { error ->
                Log.w(TAG, "Falha ao registrar token FCM no backend: ${error.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val tipo = message.data["tipo"] ?: "SISTEMA"

        when (tipo) {
            "CORRIDA_NOVA" -> showNotification(
                channelId = Constants.NOTIFICATION_CHANNEL_CORRIDA,
                title = message.data["titulo"] ?: "Nova corrida disponível!",
                body = message.data["corpo"] ?: "Uma nova corrida está disponível para você.",
                notificationId = 2001
            )
            "MENSAGEM" -> showNotification(
                channelId = Constants.NOTIFICATION_CHANNEL_CHAT,
                title = message.data["titulo"] ?: "Nova mensagem",
                body = message.data["corpo"] ?: "Você recebeu uma nova mensagem.",
                notificationId = 3001
            )
            else -> showNotification(
                channelId = Constants.NOTIFICATION_CHANNEL_SISTEMA,
                title = message.notification?.title ?: "Pyloto",
                body = message.notification?.body ?: "",
                notificationId = 4001
            )
        }
    }

    private fun showNotification(
        channelId: String,
        title: String,
        body: String,
        notificationId: Int
    ) {
        createNotificationChannel(channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_location) // TODO: Usar ícone do Pyloto
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }

    private fun createNotificationChannel(channelId: String) {
        val name = when (channelId) {
            Constants.NOTIFICATION_CHANNEL_CORRIDA -> "Corridas"
            Constants.NOTIFICATION_CHANNEL_CHAT -> "Mensagens"
            else -> "Sistema"
        }

        val importance = when (channelId) {
            Constants.NOTIFICATION_CHANNEL_CORRIDA -> NotificationManager.IMPORTANCE_HIGH
            Constants.NOTIFICATION_CHANNEL_CHAT -> NotificationManager.IMPORTANCE_DEFAULT
            else -> NotificationManager.IMPORTANCE_LOW
        }

        val channel = NotificationChannel(channelId, name, importance)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FirebaseServiceEntryPoint {
        fun apiService(): ApiService
    }

    private companion object {
        const val TAG = "PylotoFcmService"
    }
}
