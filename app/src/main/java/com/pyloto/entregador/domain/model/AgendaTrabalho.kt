package com.pyloto.entregador.domain.model

data class AgendaTrabalho(
    val dataAtual: String,
    val janelaAberta: List<String>,
    val operacaoHoje: AgendaOperacaoHoje,
    val bloqueioAbertura: AgendaBloqueioAbertura,
    val dias: List<AgendaDia>,
    val historico: List<AgendaHistorico>,
    val penalidadesAtivas: List<AgendaPenalidade>
)

data class AgendaOperacaoHoje(
    val data: String,
    val bucket: String,
    val priorityReleaseDelaySeconds: Int,
    val message: String,
    val scheduleEntryId: String?
)

data class AgendaBloqueioAbertura(
    val ativo: Boolean,
    val motivo: String,
    val dataReferencia: String
)

data class AgendaDia(
    val data: String,
    val titulo: String,
    val inicioLocal: String,
    val fimLocal: String,
    val status: String,
    val agendamentoId: String?,
    val canSchedule: Boolean,
    val canCancel: Boolean,
    val mensagem: String?
)

data class AgendaHistorico(
    val id: String,
    val data: String,
    val inicioLocal: String,
    val fimLocal: String,
    val status: String,
    val cancelamentoSemPenalidade: Boolean?,
    val comparecimentoRegistradoEm: String?,
    val comparecimentoOrigem: String?,
    val motivoCancelamento: String?,
    val penalidadeId: String?
)

data class AgendaPenalidade(
    val id: String,
    val tipo: String,
    val status: String,
    val referenciaAgendamentoId: String?,
    val referenciaData: String,
    val bloqueiaAberturaEm: String,
    val motivo: String?
)
