package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entregador")
data class EntregadorEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val fotoUrl: String?,
    val veiculoTipo: String?, // MOTO, BICICLETA, CARRO
    val veiculoPlaca: String?,
    val rating: Double,
    val totalCorridas: Int,
    val statusOnline: Boolean,
    val atualizadoEm: Long = System.currentTimeMillis()
)
