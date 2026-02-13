package com.pyloto.entregador.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun saveLocation(location: Location)
    suspend fun syncLocationToServer(location: Location)
    suspend fun syncPendingLocations()
    fun getLastLocation(): Flow<Location?>
    suspend fun getLastKnownLocation(): Location?
}
