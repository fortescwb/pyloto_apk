package com.pyloto.entregador.data.notificacao.remote.dto

import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    @SerializedName("token") val token: String,
    @SerializedName("platform") val platform: String = "ANDROID",
    @SerializedName("device_id") val deviceId: String? = null,
    @SerializedName("app_version") val appVersion: String? = null,
    @SerializedName("push_enabled") val pushEnabled: Boolean = true
)

data class NotificacaoResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "titulo", alternate = ["title"]) val titulo: String,
    @SerializedName(value = "corpo", alternate = ["message", "mensagem"]) val corpo: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("dados") val dados: Map<String, String>? = null,
    @SerializedName(value = "timestamp", alternate = ["created_at"]) val timestamp: Double,
    @SerializedName("lida") val lida: Boolean = false,
    @SerializedName("lida_em") val lidaEm: Double? = null,
    @SerializedName("unread_count") val unreadCount: Int? = null,
    @SerializedName("has_unread") val hasUnread: Boolean? = null
)

data class NotificacaoUnreadCountResponse(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("has_unread") val hasUnread: Boolean = false
)

data class NotificacaoReadResponse(
    @SerializedName("id") val id: String,
    @SerializedName("lida") val lida: Boolean = true,
    @SerializedName("lida_em") val lidaEm: Double? = null,
    @SerializedName("ja_estava_lida") val jaEstavaLida: Boolean = false,
    @SerializedName("unread_count") val unreadCount: Int? = null,
    @SerializedName("has_unread") val hasUnread: Boolean? = null
)

data class NotificacaoReadAllResponse(
    @SerializedName("updated") val updated: Int = 0,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("has_unread") val hasUnread: Boolean = false
)
