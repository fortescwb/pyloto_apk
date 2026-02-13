package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.MensagemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MensagemDao {

    @Query("SELECT * FROM mensagens WHERE corridaId = :corridaId ORDER BY timestamp ASC")
    fun observarMensagens(corridaId: String): Flow<List<MensagemEntity>>

    @Query("SELECT * FROM mensagens WHERE corridaId = :corridaId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMensagensPaginadas(corridaId: String, limit: Int = 20, offset: Int = 0): List<MensagemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mensagem: MensagemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mensagens: List<MensagemEntity>)

    @Query("UPDATE mensagens SET lida = 1 WHERE corridaId = :corridaId AND lida = 0")
    suspend fun marcarTodasComoLidas(corridaId: String)

    @Query("SELECT COUNT(*) FROM mensagens WHERE corridaId = :corridaId AND lida = 0 AND remetenteTipo != 'ENTREGADOR'")
    fun contarNaoLidas(corridaId: String): Flow<Int>

    @Query("SELECT * FROM mensagens WHERE sincronizada = 0")
    suspend fun getNaoSincronizadas(): List<MensagemEntity>
}
