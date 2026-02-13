package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.CorridaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CorridaDao {

    @Query("SELECT * FROM corridas WHERE status = 'DISPONIVEL' ORDER BY criadoEm DESC")
    suspend fun getDisponiveis(): List<CorridaEntity>

    @Query("SELECT * FROM corridas WHERE status IN ('ACEITA', 'A_CAMINHO_COLETA', 'COLETADA', 'A_CAMINHO_ENTREGA') LIMIT 1")
    fun observarCorridaAtiva(): Flow<CorridaEntity?>

    @Query("SELECT * FROM corridas WHERE status IN ('ACEITA', 'A_CAMINHO_COLETA', 'COLETADA', 'A_CAMINHO_ENTREGA') LIMIT 1")
    suspend fun getCorridaAtiva(): CorridaEntity?

    @Query("SELECT * FROM corridas WHERE id = :corridaId")
    suspend fun getById(corridaId: String): CorridaEntity?

    @Query("SELECT * FROM corridas WHERE id = :corridaId")
    fun observarPorId(corridaId: String): Flow<CorridaEntity?>

    @Query("SELECT * FROM corridas WHERE status = 'FINALIZADA' ORDER BY finalizadaEm DESC")
    fun observarHistorico(): Flow<List<CorridaEntity>>

    @Query("SELECT * FROM corridas WHERE sincronizado = 0")
    suspend fun getNaoSincronizadas(): List<CorridaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(corrida: CorridaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(corridas: List<CorridaEntity>)

    @Update
    suspend fun update(corrida: CorridaEntity)

    @Query("UPDATE corridas SET status = :status, atualizadoEm = :timestamp WHERE id = :corridaId")
    suspend fun atualizarStatus(corridaId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE corridas SET sincronizado = 1 WHERE id = :corridaId")
    suspend fun marcarSincronizada(corridaId: String)

    @Query("DELETE FROM corridas WHERE status = 'DISPONIVEL' AND criadoEm < :timestamp")
    suspend fun limparDisponiveisAntigas(timestamp: Long)

    @Query("SELECT COUNT(*) FROM corridas WHERE status = 'FINALIZADA'")
    fun contarFinalizadas(): Flow<Int>
}
