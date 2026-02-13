package com.pyloto.entregador.data.repository

import android.location.Location
import com.pyloto.entregador.core.database.dao.LocationDao
import com.pyloto.entregador.core.database.entity.LocationEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.network.ConnectivityMonitor
import com.pyloto.entregador.data.remote.model.LocationUpdate
import com.pyloto.entregador.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val locationDao: LocationDao,
    private val connectivityMonitor: ConnectivityMonitor
) : LocationRepository {

    override suspend fun saveLocation(location: Location) {
        val entity = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            speed = location.speed,
            bearing = location.bearing,
            altitude = location.altitude,
            timestamp = location.time,
            corridaId = null, // Será preenchido se houver corrida ativa
            sincronizado = false
        )
        locationDao.insert(entity)
    }

    override suspend fun syncLocationToServer(location: Location) {
        if (!connectivityMonitor.isCurrentlyConnected()) return

        try {
            val update = LocationUpdate(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                speed = location.speed,
                bearing = location.bearing,
                timestamp = location.time
            )
            apiService.atualizarLocalizacao(update)
        } catch (e: Exception) {
            // Falha silenciosa - será sincronizado via batch no WorkManager
        }
    }

    override suspend fun syncPendingLocations() {
        if (!connectivityMonitor.isCurrentlyConnected()) return

        val pendentes = locationDao.getNaoSincronizadas(100)
        if (pendentes.isEmpty()) return

        try {
            val updates = pendentes.map { entity ->
                LocationUpdate(
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    accuracy = entity.accuracy,
                    speed = entity.speed,
                    bearing = entity.bearing,
                    timestamp = entity.timestamp
                )
            }
            apiService.atualizarLocalizacaoBatch(updates)
            locationDao.marcarSincronizadas(pendentes.map { it.id })
        } catch (e: Exception) {
            // Retry na próxima execução do WorkManager
        }
    }

    override fun getLastLocation(): Flow<Location?> {
        return locationDao.observarUltimaLocalizacao().map { entity ->
            entity?.let {
                Location("cached").apply {
                    latitude = it.latitude
                    longitude = it.longitude
                    accuracy = it.accuracy
                    speed = it.speed
                    bearing = it.bearing
                    altitude = it.altitude
                    time = it.timestamp
                }
            }
        }
    }

    override suspend fun getLastKnownLocation(): Location? {
        val entity = locationDao.getUltimaLocalizacao() ?: return null
        return Location("cached").apply {
            latitude = entity.latitude
            longitude = entity.longitude
            accuracy = entity.accuracy
            speed = entity.speed
            bearing = entity.bearing
            altitude = entity.altitude
            time = entity.timestamp
        }
    }
}
