package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.EntregadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregadorDao {

    @Query("SELECT * FROM entregador LIMIT 1")
    suspend fun getEntregador(): EntregadorEntity?

    @Query("SELECT * FROM entregador LIMIT 1")
    fun observarEntregador(): Flow<EntregadorEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entregador: EntregadorEntity)

    @Update
    suspend fun update(entregador: EntregadorEntity)

    @Query("UPDATE entregador SET statusOnline = :online, atualizadoEm = :timestamp")
    suspend fun atualizarStatusOnline(online: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM entregador")
    suspend fun limpar()
}
