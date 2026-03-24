package com.pyloto.entregador.data.entregador.remote.dto

import com.google.gson.annotations.SerializedName
import com.pyloto.entregador.domain.model.AgendaBloqueioAbertura
import com.pyloto.entregador.domain.model.AgendaDia
import com.pyloto.entregador.domain.model.AgendaHistorico
import com.pyloto.entregador.domain.model.AgendaOperacaoHoje
import com.pyloto.entregador.domain.model.AgendaPenalidade
import com.pyloto.entregador.domain.model.AgendaTrabalho

data class AgendaTrabalhoResponse(
    @SerializedName("data_atual")
    val dataAtual: String = "",
    @SerializedName("janela_aberta")
    val janelaAberta: List<String> = emptyList(),
    @SerializedName("operacao_hoje")
    val operacaoHoje: AgendaOperacaoHojeResponse = AgendaOperacaoHojeResponse(),
    @SerializedName("bloqueio_abertura")
    val bloqueioAbertura: AgendaBloqueioAberturaResponse = AgendaBloqueioAberturaResponse(),
    @SerializedName("dias")
    val dias: List<AgendaDiaResponse> = emptyList(),
    @SerializedName("historico")
    val historico: List<AgendaHistoricoResponse> = emptyList(),
    @SerializedName("penalidades_ativas")
    val penalidadesAtivas: List<AgendaPenalidadeResponse> = emptyList()
)

data class AgendaOperacaoHojeResponse(
    @SerializedName("data")
    val data: String = "",
    @SerializedName("bucket")
    val bucket: String = "",
    @SerializedName("priority_release_delay_seconds")
    val priorityReleaseDelaySeconds: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("schedule_entry_id")
    val scheduleEntryId: String? = null
)

data class AgendaBloqueioAberturaResponse(
    @SerializedName("ativo")
    val ativo: Boolean = false,
    @SerializedName("motivo")
    val motivo: String = "",
    @SerializedName("data_referencia")
    val dataReferencia: String = ""
)

data class AgendaDiaResponse(
    @SerializedName("data")
    val data: String = "",
    @SerializedName("titulo")
    val titulo: String = "",
    @SerializedName("inicio_local")
    val inicioLocal: String = "",
    @SerializedName("fim_local")
    val fimLocal: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("agendamento_id")
    val agendamentoId: String? = null,
    @SerializedName("can_schedule")
    val canSchedule: Boolean = false,
    @SerializedName("can_cancel")
    val canCancel: Boolean = false,
    @SerializedName("mensagem")
    val mensagem: String? = null
)

data class AgendaHistoricoResponse(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("data")
    val data: String = "",
    @SerializedName("inicio_local")
    val inicioLocal: String = "",
    @SerializedName("fim_local")
    val fimLocal: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("cancelamento_sem_penalidade")
    val cancelamentoSemPenalidade: Boolean? = null,
    @SerializedName("comparecimento_registrado_em")
    val comparecimentoRegistradoEm: String? = null,
    @SerializedName("comparecimento_origem")
    val comparecimentoOrigem: String? = null,
    @SerializedName("motivo_cancelamento")
    val motivoCancelamento: String? = null,
    @SerializedName("penalidade_id")
    val penalidadeId: String? = null
)

data class AgendaPenalidadeResponse(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("tipo")
    val tipo: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("referencia_agendamento_id")
    val referenciaAgendamentoId: String? = null,
    @SerializedName("referencia_data")
    val referenciaData: String = "",
    @SerializedName("bloqueia_abertura_em")
    val bloqueiaAberturaEm: String = "",
    @SerializedName("motivo")
    val motivo: String? = null
)

data class CriarAgendaRequest(
    @SerializedName("data")
    val data: String
)

data class CancelarAgendaRequest(
    @SerializedName("motivo")
    val motivo: String? = null
)

fun AgendaTrabalhoResponse.toDomain(): AgendaTrabalho {
    return AgendaTrabalho(
        dataAtual = dataAtual,
        janelaAberta = janelaAberta,
        operacaoHoje = AgendaOperacaoHoje(
            data = operacaoHoje.data,
            bucket = operacaoHoje.bucket,
            priorityReleaseDelaySeconds = operacaoHoje.priorityReleaseDelaySeconds,
            message = operacaoHoje.message,
            scheduleEntryId = operacaoHoje.scheduleEntryId
        ),
        bloqueioAbertura = AgendaBloqueioAbertura(
            ativo = bloqueioAbertura.ativo,
            motivo = bloqueioAbertura.motivo,
            dataReferencia = bloqueioAbertura.dataReferencia
        ),
        dias = dias.map { day ->
            AgendaDia(
                data = day.data,
                titulo = day.titulo,
                inicioLocal = day.inicioLocal,
                fimLocal = day.fimLocal,
                status = day.status,
                agendamentoId = day.agendamentoId,
                canSchedule = day.canSchedule,
                canCancel = day.canCancel,
                mensagem = day.mensagem
            )
        },
        historico = historico.map { item ->
            AgendaHistorico(
                id = item.id,
                data = item.data,
                inicioLocal = item.inicioLocal,
                fimLocal = item.fimLocal,
                status = item.status,
                cancelamentoSemPenalidade = item.cancelamentoSemPenalidade,
                comparecimentoRegistradoEm = item.comparecimentoRegistradoEm,
                comparecimentoOrigem = item.comparecimentoOrigem,
                motivoCancelamento = item.motivoCancelamento,
                penalidadeId = item.penalidadeId
            )
        },
        penalidadesAtivas = penalidadesAtivas.map { item ->
            AgendaPenalidade(
                id = item.id,
                tipo = item.tipo,
                status = item.status,
                referenciaAgendamentoId = item.referenciaAgendamentoId,
                referenciaData = item.referenciaData,
                bloqueiaAberturaEm = item.bloqueiaAberturaEm,
                motivo = item.motivo
            )
        }
    )
}
