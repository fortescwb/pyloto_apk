package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDENTE' ORDER BY prioridade DESC, criadoEm ASC")
    suspend fun getPendentes(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE status = 'FALHA' AND tentativas < maxTentativas ORDER BY prioridade DESC")
    suspend fun getParaRetentativa(): List<SyncQueueEntity>

    @Insert
    suspend fun insert(item: SyncQueueEntity): Long

    @Update
    suspend fun update(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = :status, tentativas = tentativas + 1, ultimaTentativaEm = :timestamp, erroMensagem = :erro WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: String, timestamp: Long = System.currentTimeMillis(), erro: String? = null)

    @Query("DELETE FROM sync_queue WHERE status = 'CONCLUIDO'")
    suspend fun limparConcluidos()

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status IN ('PENDENTE', 'FALHA')")
    suspend fun contarPendentes(): Int
}
