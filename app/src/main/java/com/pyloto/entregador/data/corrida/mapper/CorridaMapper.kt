package com.pyloto.entregador.data.corrida.mapper

import com.pyloto.entregador.core.database.entity.CorridaEntity
import com.pyloto.entregador.data.corrida.remote.dto.CorridaDetalhesResponse
import com.pyloto.entregador.data.corrida.remote.dto.CorridaResponse
import com.pyloto.entregador.data.corrida.remote.dto.EnderecoResponse
import com.pyloto.entregador.domain.model.Cliente
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.model.CorridaTimestamps
import com.pyloto.entregador.domain.model.Endereco
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Mapper para converter entre as camadas (Remote <-> Entity <-> Domain).
 */
class CorridaMapper @Inject constructor() {

    fun toDomain(response: CorridaResponse): Corrida {
        return toDomainInternal(
            id = response.id,
            clienteNome = response.clienteNome,
            clienteTelefone = response.clienteTelefone,
            clienteFoto = response.clienteFoto,
            enderecoOrigem = response.enderecoOrigem,
            enderecoDestino = response.enderecoDestino,
            valorEntrega = response.valorEntrega,
            distanciaKm = response.distanciaKm,
            tempoEstimadoMin = response.tempoEstimadoMin,
            status = response.status,
            criadoEm = response.criadoEm,
            aceitaEm = response.aceitaEm,
            iniciadaEm = response.iniciadaEm,
            coletadaEm = response.coletadaEm,
            finalizadaEm = response.finalizadaEm,
            canceladaEm = response.canceladaEm,
            fotoComprovanteUrl = response.fotoComprovanteUrl,
            motivoCancelamento = response.motivoCancelamento,
            dados = response.dados
        )
    }

    fun toDomain(response: CorridaDetalhesResponse): Corrida {
        return toDomainInternal(
            id = response.id,
            clienteNome = response.clienteNome,
            clienteTelefone = response.clienteTelefone,
            clienteFoto = response.clienteFoto,
            enderecoOrigem = response.enderecoOrigem,
            enderecoDestino = response.enderecoDestino,
            valorEntrega = response.valorEntrega,
            distanciaKm = response.distanciaKm,
            tempoEstimadoMin = response.tempoEstimadoMin,
            status = response.status,
            criadoEm = response.criadoEm,
            aceitaEm = response.aceitaEm,
            iniciadaEm = response.iniciadaEm,
            coletadaEm = response.coletadaEm,
            finalizadaEm = response.finalizadaEm,
            canceladaEm = response.canceladaEm,
            fotoComprovanteUrl = response.fotoComprovanteUrl,
            motivoCancelamento = response.motivoCancelamento,
            dados = response.dados
        )
    }

    fun toDomain(entity: CorridaEntity): Corrida {
        return Corrida(
            id = entity.id,
            cliente = Cliente(
                nome = entity.clienteNome,
                telefone = entity.clienteTelefone,
                foto = entity.clienteFoto
            ),
            origem = Endereco(
                logradouro = entity.enderecoOrigem,
                numero = "",
                bairro = "",
                cidade = "",
                cep = "",
                latitude = entity.latOrigem,
                longitude = entity.lngOrigem
            ),
            destino = Endereco(
                logradouro = entity.enderecoDestino,
                numero = "",
                bairro = "",
                cidade = "",
                cep = "",
                latitude = entity.latDestino,
                longitude = entity.lngDestino
            ),
            valor = BigDecimal.valueOf(entity.valorEntrega),
            distanciaKm = entity.distanciaKm,
            tempoEstimadoMin = entity.tempoEstimadoMin,
            status = mapStatus(entity.status),
            timestamps = CorridaTimestamps(
                criadaEm = entity.criadoEm,
                aceitaEm = entity.aceitaEm,
                iniciadaEm = entity.iniciadaEm,
                coletadaEm = entity.coletadaEm,
                finalizadaEm = entity.finalizadaEm,
                canceladaEm = entity.canceladaEm
            ),
            fotoComprovanteUrl = entity.fotoComprovanteUrl,
            motivoCancelamento = entity.motivoCancelamento
        )
    }

    fun toEntity(corrida: Corrida): CorridaEntity {
        return CorridaEntity(
            id = corrida.id,
            clienteNome = corrida.cliente.nome,
            clienteTelefone = corrida.cliente.telefone,
            clienteFoto = corrida.cliente.foto,
            enderecoOrigem = corrida.origem.enderecoFormatado,
            enderecoDestino = corrida.destino.enderecoFormatado,
            latOrigem = corrida.origem.latitude,
            lngOrigem = corrida.origem.longitude,
            latDestino = corrida.destino.latitude,
            lngDestino = corrida.destino.longitude,
            valorEntrega = corrida.valor.toDouble(),
            distanciaKm = corrida.distanciaKm,
            tempoEstimadoMin = corrida.tempoEstimadoMin,
            status = corrida.status.name,
            aceitaEm = corrida.timestamps.aceitaEm,
            iniciadaEm = corrida.timestamps.iniciadaEm,
            coletadaEm = corrida.timestamps.coletadaEm,
            finalizadaEm = corrida.timestamps.finalizadaEm,
            canceladaEm = corrida.timestamps.canceladaEm,
            motivoCancelamento = corrida.motivoCancelamento,
            fotoComprovanteUrl = corrida.fotoComprovanteUrl,
            sincronizado = true,
            criadoEm = corrida.timestamps.criadaEm
        )
    }

    private fun toDomainInternal(
        id: String,
        clienteNome: String?,
        clienteTelefone: String?,
        clienteFoto: String?,
        enderecoOrigem: EnderecoResponse?,
        enderecoDestino: EnderecoResponse?,
        valorEntrega: Double?,
        distanciaKm: Double?,
        tempoEstimadoMin: Int?,
        status: String,
        criadoEm: Double?,
        aceitaEm: Double?,
        iniciadaEm: Double?,
        coletadaEm: Double?,
        finalizadaEm: Double?,
        canceladaEm: Double?,
        fotoComprovanteUrl: String?,
        motivoCancelamento: String?,
        dados: Map<String, Any?>?
    ): Corrida {
        val dadosOrigem = dados?.get("origem") as? Map<*, *>
        val dadosDestino = dados?.get("destino") as? Map<*, *>

        val nome = clienteNome
            ?: extractString(dados, "nome", "solicitante_nome", "cliente_nome")
            ?: "Cliente"

        val telefone = clienteTelefone
            ?: extractString(dados, "telefone", "cliente_telefone")
            ?: ""

        val origem = toEndereco(enderecoOrigem, dadosOrigem)
        val destino = toEndereco(enderecoDestino, dadosDestino)

        return Corrida(
            id = id,
            cliente = Cliente(
                nome = nome,
                telefone = telefone,
                foto = clienteFoto
            ),
            origem = origem,
            destino = destino,
            valor = BigDecimal.valueOf(valorEntrega ?: 0.0),
            distanciaKm = distanciaKm ?: 0.0,
            tempoEstimadoMin = tempoEstimadoMin ?: 0,
            status = mapStatus(status),
            timestamps = CorridaTimestamps(
                criadaEm = criadoEm?.let(::toEpochMillis) ?: System.currentTimeMillis(),
                aceitaEm = aceitaEm?.let(::toEpochMillis),
                iniciadaEm = iniciadaEm?.let(::toEpochMillis),
                coletadaEm = coletadaEm?.let(::toEpochMillis),
                finalizadaEm = finalizadaEm?.let(::toEpochMillis),
                canceladaEm = canceladaEm?.let(::toEpochMillis)
            ),
            fotoComprovanteUrl = fotoComprovanteUrl,
            motivoCancelamento = motivoCancelamento
        )
    }

    private fun toEndereco(
        endereco: EnderecoResponse?,
        fallback: Map<*, *>?
    ): Endereco {
        val logradouro = endereco?.logradouro
            ?: fallback?.get("logradouro")?.toString()
            ?: fallback?.get("rua")?.toString()
            ?: ""

        val numero = endereco?.numero
            ?: fallback?.get("numero")?.toString()
            ?: ""

        val complemento = endereco?.complemento
            ?: fallback?.get("complemento")?.toString()

        val bairro = endereco?.bairro
            ?: fallback?.get("bairro")?.toString()
            ?: ""

        val cidade = endereco?.cidade
            ?: fallback?.get("cidade")?.toString()
            ?: ""

        val cep = endereco?.cep
            ?: fallback?.get("cep")?.toString()
            ?: ""

        val latitude = endereco?.latitude
            ?: fallback?.get("latitude")?.toString()?.toDoubleOrNull()
            ?: fallback?.get("lat")?.toString()?.toDoubleOrNull()
            ?: 0.0

        val longitude = endereco?.longitude
            ?: fallback?.get("longitude")?.toString()?.toDoubleOrNull()
            ?: fallback?.get("lng")?.toString()?.toDoubleOrNull()
            ?: 0.0

        return Endereco(
            logradouro = logradouro,
            numero = numero,
            complemento = complemento,
            bairro = bairro,
            cidade = cidade,
            cep = cep,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun extractString(map: Map<String, Any?>?, vararg keys: String): String? {
        if (map == null) return null
        for (key in keys) {
            val value = map[key]?.toString()?.trim()
            if (!value.isNullOrEmpty()) return value
        }
        return null
    }

    private fun mapStatus(rawStatus: String): CorridaStatus {
        return when (rawStatus.trim().lowercase()) {
            "disponivel", "aguardando_pagamento", "pagamento_aprovado" -> CorridaStatus.DISPONIVEL
            "aceito", "aceita" -> CorridaStatus.ACEITA
            "coletando", "a_caminho_coleta" -> CorridaStatus.A_CAMINHO_COLETA
            "coletado" -> CorridaStatus.COLETADA
            "em_entrega", "a_caminho_entrega" -> CorridaStatus.A_CAMINHO_ENTREGA
            "entregue", "finalizado", "finalizada" -> CorridaStatus.FINALIZADA
            "cancelado", "cancelada", "pagamento_recusado", "expirado" -> CorridaStatus.CANCELADA
            else -> CorridaStatus.DISPONIVEL
        }
    }

    private fun toEpochMillis(value: Double): Long {
        return if (value > 1_000_000_000_000) {
            value.toLong()
        } else {
            (value * 1000).toLong()
        }
    }
}
