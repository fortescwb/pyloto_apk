package com.pyloto.entregador.core.di

import com.pyloto.entregador.data.repository.*
import com.pyloto.entregador.domain.repository.*
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
}
