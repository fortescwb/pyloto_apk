package com.pyloto.entregador.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pyloto_entregador.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCorridaDao(db: AppDatabase) = db.corridaDao()

    @Provides
    fun provideLocationDao(db: AppDatabase) = db.locationDao()

    @Provides
    fun provideEntregadorDao(db: AppDatabase) = db.entregadorDao()

    @Provides
    fun provideMensagemDao(db: AppDatabase) = db.mensagemDao()

    @Provides
    fun provideNotificacaoDao(db: AppDatabase) = db.notificacaoDao()

    @Provides
    fun provideSyncQueueDao(db: AppDatabase) = db.syncQueueDao()
}
