package com.pyloto.entregador.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pyloto.entregador.core.database.converter.Converters
import com.pyloto.entregador.core.database.dao.*
import com.pyloto.entregador.core.database.entity.*

@Database(
    entities = [
        CorridaEntity::class,
        LocationEntity::class,
        EntregadorEntity::class,
        MensagemEntity::class,
        NotificacaoEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun corridaDao(): CorridaDao
    abstract fun locationDao(): LocationDao
    abstract fun entregadorDao(): EntregadorDao
    abstract fun mensagemDao(): MensagemDao
    abstract fun notificacaoDao(): NotificacaoDao
    abstract fun syncQueueDao(): SyncQueueDao
}
