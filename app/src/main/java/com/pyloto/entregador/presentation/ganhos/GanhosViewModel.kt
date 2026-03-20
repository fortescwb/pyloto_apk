package com.pyloto.entregador.presentation.ganhos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.usecase.entregador.ObterGanhosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GanhosViewModel @Inject constructor(
    private val obterGanhosUseCase: ObterGanhosUseCase,
    private val corridaRepository: CorridaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GanhosUiState())
    val uiState: StateFlow<GanhosUiState> = _uiState.asStateFlow()

    init {
        loadGanhos()
    }

    fun loadGanhos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                val periodo = _uiState.value.periodoSelecionado.toApiPeriodo()
                val ganhos = obterGanhosUseCase(periodo = periodo)
                val corridasHistorico = corridaRepository.getHistoricoPaginado(page = 0, size = 50)
                val corridasRealizadas = corridasHistorico.map(::toCorridaRealizada)

                val totalKm = corridasRealizadas.sumOf { it.distanciaKm }
                val tempoOnline = corridasRealizadas.sumOf { it.tempoMin }
                val totalLiquido = ganhos.totalLiquido.toDouble()

                val ganhoPorKm = if (totalKm > 0.0) {
                    totalLiquido / totalKm
                } else {
                    0.0
                }
                val ganhoPorHora = if (tempoOnline > 0) {
                    totalLiquido / (tempoOnline / 60.0)
                } else {
                    0.0
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ganhos = ganhos,
                        corridasRealizadas = corridasRealizadas,
                        totalKmRodados = totalKm,
                        tempoOnlineMinutos = tempoOnline,
                        ganhoPorKm = ganhoPorKm,
                        ganhoPorHora = ganhoPorHora,
                        erro = null
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = error.message ?: "Erro ao carregar ganhos"
                    )
                }
            }
        }
    }

    fun selecionarPeriodo(periodo: PeriodoGanhos) {
        _uiState.update { it.copy(periodoSelecionado = periodo) }
        loadGanhos()
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }

    private fun toCorridaRealizada(corrida: Corrida): CorridaRealizada {
        return CorridaRealizada(
            id = corrida.id,
            clienteNome = corrida.cliente.nome,
            origemBairro = corrida.origem.bairro.ifBlank { corrida.origem.logradouro },
            destinoBairro = corrida.destino.bairro.ifBlank { corrida.destino.logradouro },
            valor = corrida.valor.toDouble(),
            distanciaKm = corrida.distanciaKm,
            tempoMin = corrida.tempoEstimadoMin,
            dataHora = formatTimestamp(corrida.timestamps.finalizadaEm ?: corrida.timestamps.criadaEm),
            status = corrida.status
        )
    }

    private fun formatTimestamp(epochMillis: Long): String {
        return DateTimeFormatter.ofPattern("dd/MM - HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(epochMillis))
    }
}

