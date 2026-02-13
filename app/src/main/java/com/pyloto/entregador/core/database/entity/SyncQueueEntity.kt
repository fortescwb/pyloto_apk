package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Fila de sincronização para operações offline-first.
 * Quando não há conexão, operações são enfileiradas aqui
 * e sincronizadas quando a conexão retorna.
 * Essencial para escala e resiliência.
 */
@Entity(
    tableName = "sync_queue",
    indices = [
        Index(value = ["status"]),
        Index(value = ["prioridade"]),
        Index(value = ["criadoEm"])
    ]
)
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val operacao: String, // ATUALIZAR_LOCALIZACAO, ACEITAR_CORRIDA, FINALIZAR_CORRIDA, etc.
    val endpoint: String,
    val metodo: String, // POST, PUT, DELETE
    val payload: String, // JSON serializado
    val tentativas: Int = 0,
    val maxTentativas: Int = 5,
    val status: String = "PENDENTE", // PENDENTE, EM_PROCESSO, FALHA, CONCLUIDO
    val prioridade: Int = 0, // 0 = normal, 1 = alta, 2 = crítica
    val criadoEm: Long = System.currentTimeMillis(),
    val ultimaTentativaEm: Long? = null,
    val erroMensagem: String? = null
)
