package com.pyloto.entregador.data.entregador.mapper

import com.pyloto.entregador.core.database.entity.EntregadorEntity
import com.pyloto.entregador.data.entregador.remote.dto.AtualizarPerfilRequest
import com.pyloto.entregador.data.entregador.remote.dto.EntregadorPerfilResponse
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Veiculo
import com.pyloto.entregador.domain.model.VeiculoTipo
import javax.inject.Inject

class EntregadorMapper @Inject constructor() {

    fun toEntity(response: EntregadorPerfilResponse): EntregadorEntity {
        return EntregadorEntity(
            id = response.id,
            nome = response.nome,
            email = response.email,
            telefone = response.telefone,
            cpf = response.cpf,
            fotoUrl = response.fotoUrl,
            veiculoTipo = response.veiculoTipo?.uppercase(),
            veiculoPlaca = response.veiculoPlaca,
            rating = response.rating,
            totalCorridas = response.totalCorridas,
            statusOnline = response.statusOnline
        )
    }

    fun toDomain(entity: EntregadorEntity): Entregador {
        return Entregador(
            id = entity.id,
            nome = entity.nome,
            email = entity.email,
            telefone = entity.telefone,
            cpf = entity.cpf,
            fotoUrl = entity.fotoUrl,
            veiculo = entity.veiculoTipo
                ?.let { raw -> VeiculoTipo.values().firstOrNull { it.name == raw.uppercase() } }
                ?.let { tipo -> Veiculo(tipo = tipo, placa = entity.veiculoPlaca) },
            rating = entity.rating,
            totalCorridas = entity.totalCorridas,
            statusOnline = entity.statusOnline
        )
    }

    fun toAtualizarPerfilRequest(entregador: Entregador): AtualizarPerfilRequest {
        return AtualizarPerfilRequest(
            nome = entregador.nome,
            telefone = entregador.telefone,
            fotoUrl = null,
            veiculoTipo = entregador.veiculo?.tipo?.name,
            veiculoPlaca = entregador.veiculo?.placa
        )
    }
}
