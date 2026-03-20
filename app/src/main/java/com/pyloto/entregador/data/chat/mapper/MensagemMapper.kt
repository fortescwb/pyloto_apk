package com.pyloto.entregador.data.chat.mapper

import com.pyloto.entregador.core.database.entity.MensagemEntity
import com.pyloto.entregador.data.chat.remote.dto.MensagemResponse
import com.pyloto.entregador.domain.model.Mensagem
import com.pyloto.entregador.domain.model.RemetenteTipo
import com.pyloto.entregador.domain.model.TipoMensagem
import java.util.UUID
import javax.inject.Inject

class MensagemMapper @Inject constructor() {

    fun toEntity(
        response: MensagemResponse,
        lida: Boolean = false,
        sincronizada: Boolean = false,
        corridaIdFallback: String? = null,
        remetenteIdFallback: String? = null,
        remetenteTipoFallback: String = "ENTREGADOR"
    ): MensagemEntity {
        return MensagemEntity(
            id = response.id ?: response.messageId ?: UUID.randomUUID().toString(),
            corridaId = response.corridaId ?: corridaIdFallback ?: "",
            remetenteId = response.remetenteId ?: remetenteIdFallback ?: "",
            remetenteTipo = normalizeRemetenteTipo(response.remetenteTipo, remetenteTipoFallback),
            conteudo = response.conteudo ?: "",
            tipoMensagem = normalizeTipoMensagem(response.tipoMensagem),
            timestamp = toEpochMillis(response.timestamp),
            lida = lida,
            sincronizada = sincronizada
        )
    }

    fun toDomain(entity: MensagemEntity): Mensagem {
        return Mensagem(
            id = entity.id,
            corridaId = entity.corridaId,
            remetenteId = entity.remetenteId,
            remetenteTipo = RemetenteTipo.valueOf(entity.remetenteTipo),
            conteudo = entity.conteudo,
            tipo = TipoMensagem.valueOf(entity.tipoMensagem),
            timestamp = entity.timestamp,
            lida = entity.lida
        )
    }

    private fun normalizeRemetenteTipo(value: String?, fallback: String): String {
        return when ((value ?: fallback).trim().uppercase()) {
            "ENTREGADOR", "PARCEIRO" -> "ENTREGADOR"
            "CLIENTE", "SOLICITANTE" -> "CLIENTE"
            "SISTEMA" -> "SISTEMA"
            else -> fallback
        }
    }

    private fun normalizeTipoMensagem(value: String?): String {
        return when ((value ?: "TEXTO").trim().uppercase()) {
            "TEXTO", "TEXT" -> "TEXTO"
            "IMAGEM", "IMAGE" -> "IMAGEM"
            "LOCALIZACAO", "LOCATION" -> "LOCALIZACAO"
            else -> "TEXTO"
        }
    }

    private fun toEpochMillis(value: Double?): Long {
        if (value == null) return System.currentTimeMillis()
        return if (value > 1_000_000_000_000) value.toLong() else (value * 1000).toLong()
    }
}
