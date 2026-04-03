package com.pyloto.entregador.data.location.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.pyloto.entregador.core.database.dao.LocationDao
import com.pyloto.entregador.core.database.entity.LocationEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.network.ConnectivityMonitor
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.common.withNetworkGuard
import com.pyloto.entregador.data.location.remote.dto.LocationUpdate
import com.pyloto.entregador.domain.repository.LocationRepository
import com.pyloto.entregador.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val apiService: ApiService,
    private val locationDao: LocationDao,
    private val connectivityMonitor: ConnectivityMonitor,
    private val preferencesRepository: PreferencesRepository
) : LocationRepository {

    override suspend fun saveLocation(location: Location) {
        val activeRoute = preferencesRepository.getActiveRouteContext()
        val entity = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            accuracy = location.accuracy,
            speed = location.speed,
            bearing = location.bearing,
            altitude = location.altitude,
            timestamp = location.time,
            corridaId = activeRoute?.pedidoId,
            sincronizado = false
        )
        locationDao.insert(entity)
    }

    override suspend fun syncLocationToServer(location: Location) {
        val activeRoute = preferencesRepository.getActiveRouteContext()
        val source = if (activeRoute != null) "route_tracking" else "passive_presence"
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
                    timestamp = location.time,
                    source = source,
                    pedidoId = activeRoute?.pedidoId
                )
                apiService.atualizarLocalizacao(update)
            },
            local = { Unit }
        )
    }

    override suspend fun syncPendingLocations() {
        val pendentes = locationDao.getNaoSincronizadas(100)
        if (pendentes.isEmpty()) return
        val activeRoute = preferencesRepository.getActiveRouteContext()
        val source = if (activeRoute != null) "route_tracking_batch" else "passive_presence_batch"

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
                        timestamp = entity.timestamp,
                        source = source,
                        pedidoId = entity.corridaId ?: activeRoute?.pedidoId
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

    override suspend fun getCurrentOrLastKnownLocation(): Location? {
        val cachedLocation = getLastKnownLocation()
        if (!hasForegroundLocationPermission()) {
            return cachedLocation
        }

        val resolvedLocation = requestCurrentLocation()
            ?: requestFusedLastLocation()
            ?: cachedLocation

        if (resolvedLocation != null && shouldPersistFreshLocation(resolvedLocation, cachedLocation)) {
            saveLocation(resolvedLocation)
            syncLocationToServer(resolvedLocation)
        }
        return resolvedLocation
    }

    private fun hasForegroundLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    private suspend fun requestCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
            .addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
            .addOnCanceledListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }
    }

    private suspend fun requestFusedLastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
            .addOnFailureListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
            .addOnCanceledListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
    }

    private fun shouldPersistFreshLocation(currentLocation: Location, cachedLocation: Location?): Boolean {
        if (cachedLocation == null) {
            return true
        }
        val isNewer = currentLocation.time > cachedLocation.time
        if (!isNewer) {
            return false
        }
        val movedEnough = currentLocation.distanceTo(cachedLocation) >= MIN_PERSIST_DISTANCE_METERS
        val accuracyImprovedEnough = currentLocation.accuracy < cachedLocation.accuracy
        return movedEnough || accuracyImprovedEnough
    }

    private companion object {
        const val MIN_PERSIST_DISTANCE_METERS = 5f
    }
}

