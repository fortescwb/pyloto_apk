package com.pyloto.entregador.core.util

object Constants {
    // API
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_RETRY_ATTEMPTS = 3
    const val LOCATION_SYNC_BATCH_SIZE = 50

    // GPS
    const val DEFAULT_RAIO_BUSCA_METROS = 5000
    const val GEOFENCE_RAIO_COLETA = 200f    // metros
    const val GEOFENCE_RAIO_ENTREGA = 100f   // metros

    // Timeouts
    const val ACEITAR_CORRIDA_TIMEOUT_SECONDS = 60
    const val SYNC_INTERVAL_MINUTES = 15L

    // Cache
    const val CACHE_CORRIDAS_DISPONIVEIS_MINUTES = 2L
    const val CACHE_PERFIL_HOURS = 24L

    // Notificações
    const val NOTIFICATION_CHANNEL_CORRIDA = "corrida_channel"
    const val NOTIFICATION_CHANNEL_CHAT = "chat_channel"
    const val NOTIFICATION_CHANNEL_SISTEMA = "sistema_channel"

    // DataStore Keys
    const val PREF_ACCESS_TOKEN = "access_token"
    const val PREF_REFRESH_TOKEN = "refresh_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_ONLINE = "is_online"
    const val PREF_ONBOARDING_COMPLETE = "onboarding_complete"
    const val PREF_DAILY_GOAL = "daily_goal"
    const val PREF_WEEKLY_GOAL = "weekly_goal"
    const val PREF_ACTIVE_ROUTE_PEDIDO_ID = "active_route_pedido_id"
    const val PREF_ACTIVE_ROUTE_PHASE = "active_route_phase"
    const val PREF_ACTIVE_ROUTE_STARTED_AT = "active_route_started_at"

    // Default values
    const val DEFAULT_DAILY_GOAL = 300.0
    const val DEFAULT_WEEKLY_GOAL = 1500.0

    // WorkManager Tags
    const val WORK_SYNC_LOCATIONS = "sync_locations"
    const val WORK_SYNC_QUEUE = "sync_queue"
    const val WORK_CLEANUP_CACHE = "cleanup_cache"
}
