package com.pyloto.entregador.core.database.dao

import androidx.room.*
import com.pyloto.entregador.core.database.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: LocationEntity)

    @Insert
    suspend fun insertAll(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations WHERE sincronizado = 0 ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getNaoSincronizadas(limit: Int = 100): List<LocationEntity>

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getUltimaLocalizacao(): LocationEntity?

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    fun observarUltimaLocalizacao(): Flow<LocationEntity?>

    @Query("UPDATE locations SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun marcarSincronizadas(ids: List<Long>)

    @Query("DELETE FROM locations WHERE sincronizado = 1 AND timestamp < :timestamp")
    suspend fun limparSincronizadasAntigas(timestamp: Long)

    @Query("SELECT COUNT(*) FROM locations WHERE sincronizado = 0")
    suspend fun contarPendentes(): Int
}
