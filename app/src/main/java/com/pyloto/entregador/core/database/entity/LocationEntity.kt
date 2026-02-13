package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [
        Index(value = ["sincronizado"]),
        Index(value = ["timestamp"])
    ]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val altitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val corridaId: String?, // Associar ao contexto da corrida ativa
    val sincronizado: Boolean = false
)
