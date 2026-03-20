package com.pyloto.entregador.data.location.repository

import android.location.Location
import com.pyloto.entregador.core.database.dao.LocationDao
import com.pyloto.entregador.core.database.entity.LocationEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.network.ConnectivityMonitor
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.common.withNetworkGuard
import com.pyloto.entregador.data.location.remote.dto.LocationUpdate
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
            corridaId = null,
            sincronizado = false
        )
        locationDao.insert(entity)
    }

    override suspend fun syncLocationToServer(location: Location) {
        withNetworkGuard(
            operation = "localizacao_sync_unitaria",
            isConnected = connectivityMonitor.isCurrentlyConnected(),
            remote = {
                val update = LocationUpdate(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    speed = location.speed,
                    bearing = location.bearing,
                    timestamp = location.time
                )
                apiService.atualizarLocalizacao(update)
            },
            local = { Unit }
        )
    }

    override suspend fun syncPendingLocations() {
        val pendentes = locationDao.getNaoSincronizadas(100)
        if (pendentes.isEmpty()) return

        withNetworkGuard(
            operation = "localizacao_sync_lote",
            isConnected = connectivityMonitor.isCurrentlyConnected(),
            remote = {
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
                withCacheFallback(
                    operation = "localizacao_sync_lote_request",
                    remote = {
                        apiService.atualizarLocalizacaoBatch(updates)
                        locationDao.marcarSincronizadas(pendentes.map { it.id })
                    },
                    local = { Unit }
                )
            },
            local = { Unit }
        )
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

