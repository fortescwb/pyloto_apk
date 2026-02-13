package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.NotificacaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacaoDao {

    @Query("SELECT * FROM notificacoes ORDER BY timestamp DESC")
    fun observarTodas(): Flow<List<NotificacaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificacao: NotificacaoEntity)

    @Query("UPDATE notificacoes SET lida = 1 WHERE id = :id")
    suspend fun marcarComoLida(id: String)

    @Query("UPDATE notificacoes SET lida = 1")
    suspend fun marcarTodasComoLidas()

    @Query("SELECT COUNT(*) FROM notificacoes WHERE lida = 0")
    fun contarNaoLidas(): Flow<Int>

    @Query("DELETE FROM notificacoes WHERE timestamp < :timestamp")
    suspend fun limparAntigas(timestamp: Long)
}
