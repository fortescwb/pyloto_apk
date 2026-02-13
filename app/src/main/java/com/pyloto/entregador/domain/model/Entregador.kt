package com.pyloto.entregador.domain.model

data class Entregador(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val cpf: String,
    val fotoUrl: String?,
    val veiculo: Veiculo?,
    val rating: Double,
    val totalCorridas: Int,
    val statusOnline: Boolean
)

data class Veiculo(
    val tipo: VeiculoTipo,
    val placa: String?
)

enum class VeiculoTipo {
    MOTO,
    BICICLETA,
    CARRO
}
