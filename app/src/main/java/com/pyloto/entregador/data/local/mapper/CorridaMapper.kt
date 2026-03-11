package com.pyloto.entregador.data.local.mapper

import com.pyloto.entregador.core.database.entity.CorridaEntity
import com.pyloto.entregador.data.remote.model.CorridaDetalhesResponse
import com.pyloto.entregador.data.remote.model.CorridaResponse
import com.pyloto.entregador.domain.model.*
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Mapper para converter entre as camadas (Remote ↔ Entity ↔ Domain).
 * Centraliza transformações para consistência e manutenibilidade.
 */
class CorridaMapper @Inject constructor() {

    // Remote → Domain
    fun toDomain(response: CorridaResponse): Corrida {
        return Corrida(
            id = response.id,
            cliente = Cliente(
                nome = response.clienteNome,
                telefone = response.clienteTelefone,
                foto = response.clienteFoto
            ),
            origem = Endereco(
                logradouro = response.enderecoOrigem.logradouro,
                numero = response.enderecoOrigem.numero,
                complemento = response.enderecoOrigem.complemento,
                bairro = response.enderecoOrigem.bairro,
                cidade = response.enderecoOrigem.cidade,
                cep = response.enderecoOrigem.cep,
                latitude = response.enderecoOrigem.latitude,
                longitude = response.enderecoOrigem.longitude
            ),
            destino = Endereco(
                logradouro = response.enderecoDestino.logradouro,
                numero = response.enderecoDestino.numero,
                complemento = response.enderecoDestino.complemento,
                bairro = response.enderecoDestino.bairro,
                cidade = response.enderecoDestino.cidade,
                cep = response.enderecoDestino.cep,
                latitude = response.enderecoDestino.latitude,
                longitude = response.enderecoDestino.longitude
            ),
            valor = BigDecimal.valueOf(response.valorEntrega),
            distanciaKm = response.distanciaKm,
            tempoEstimadoMin = response.tempoEstimadoMin,
            status = CorridaStatus.valueOf(response.status),
            timestamps = CorridaTimestamps(
                criadaEm = response.criadoEm,
                aceitaEm = response.aceitaEm,
                iniciadaEm = response.iniciadaEm,
                coletadaEm = response.coletadaEm,
                finalizadaEm = response.finalizadaEm,
                canceladaEm = response.canceladaEm
            ),
            fotoComprovanteUrl = response.fotoComprovanteUrl,
            motivoCancelamento = response.motivoCancelamento
        )
    }

    // Remote (Detalhes) → Domain
    fun toDomain(response: CorridaDetalhesResponse): Corrida {
        return Corrida(
            id = response.id,
            cliente = Cliente(
                nome = response.clienteNome,
                telefone = response.clienteTelefone,
                foto = response.clienteFoto
            ),
            origem = Endereco(
                logradouro = response.enderecoOrigem.logradouro,
                numero = response.enderecoOrigem.numero,
                complemento = response.enderecoOrigem.complemento,
                bairro = response.enderecoOrigem.bairro,
                cidade = response.enderecoOrigem.cidade,
                cep = response.enderecoOrigem.cep,
                latitude = response.enderecoOrigem.latitude,
                longitude = response.enderecoOrigem.longitude
            ),
            destino = Endereco(
                logradouro = response.enderecoDestino.logradouro,
                numero = response.enderecoDestino.numero,
                complemento = response.enderecoDestino.complemento,
                bairro = response.enderecoDestino.bairro,
                cidade = response.enderecoDestino.cidade,
                cep = response.enderecoDestino.cep,
                latitude = response.enderecoDestino.latitude,
                longitude = response.enderecoDestino.longitude
            ),
            valor = BigDecimal.valueOf(response.valorEntrega),
            distanciaKm = response.distanciaKm,
            tempoEstimadoMin = response.tempoEstimadoMin,
            status = CorridaStatus.valueOf(response.status),
            timestamps = CorridaTimestamps(
                criadaEm = response.criadoEm,
                aceitaEm = response.aceitaEm,
                iniciadaEm = response.iniciadaEm,
                coletadaEm = response.coletadaEm,
                finalizadaEm = response.finalizadaEm,
                canceladaEm = response.canceladaEm
            ),
            fotoComprovanteUrl = response.fotoComprovanteUrl,
            motivoCancelamento = response.motivoCancelamento
        )
    }

    // Entity → Domain
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
            status = CorridaStatus.valueOf(entity.status),
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

    // Domain → Entity
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
}
