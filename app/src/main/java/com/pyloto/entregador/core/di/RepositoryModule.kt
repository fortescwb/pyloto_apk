package com.pyloto.entregador.core.di

import com.pyloto.entregador.data.auth.repository.AuthRepositoryImpl
import com.pyloto.entregador.data.chat.repository.ChatRepositoryImpl
import com.pyloto.entregador.data.corrida.repository.CorridaRepositoryImpl
import com.pyloto.entregador.data.entregador.repository.EntregadorRepositoryImpl
import com.pyloto.entregador.data.location.repository.LocationRepositoryImpl
import com.pyloto.entregador.data.notificacao.repository.NotificacaoRepositoryImpl
import com.pyloto.entregador.data.preferences.repository.PreferencesRepositoryImpl
import com.pyloto.entregador.domain.repository.AuthRepository
import com.pyloto.entregador.domain.repository.ChatRepository
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.EntregadorRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import com.pyloto.entregador.domain.repository.NotificacaoRepository
import com.pyloto.entregador.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo principal de injeção de dependência.
 * Vincula interfaces (domain) às implementações (data).
 * Facilita testes e troca de implementações para escala.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCorridaRepository(
        impl: CorridaRepositoryImpl
    ): CorridaRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindEntregadorRepository(
        impl: EntregadorRepositoryImpl
    ): EntregadorRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindNotificacaoRepository(
        impl: NotificacaoRepositoryImpl
    ): NotificacaoRepository
}
