package com.pyloto.entregador.presentation.ganhos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Cliente
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.model.CorridaTimestamps
import com.pyloto.entregador.domain.model.Endereco
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.GanhosDia
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.EntregadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════
// PERÍODO SELECIONÁVEL
// ═══════════════════════════════════════════════════════════════

enum class PeriodoGanhos(val label: String) {
    HOJE("Hoje"),
    SEMANA("Semana"),
    MES("Mês"),
    TOTAL("Total")
}

// ═══════════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════════

data class GanhosUiState(
    val isLoading: Boolean = true,
    val periodoSelecionado: PeriodoGanhos = PeriodoGanhos.SEMANA,
    val ganhos: Ganhos? = null,
    val corridasRealizadas: List<CorridaRealizada> = emptyList(),
    val totalKmRodados: Double = 0.0,
    val tempoOnlineMinutos: Int = 0,
    val ganhoPorKm: Double = 0.0,
    val ganhoPorHora: Double = 0.0,
    val erro: String? = null
)

/**
 * Corrida finalizada para exibição no extrato.
 */
data class CorridaRealizada(
    val id: String,
    val clienteNome: String,
    val origemBairro: String,
    val destinoBairro: String,
    val valor: Double,
    val distanciaKm: Double,
    val tempoMin: Int,
    val dataHora: String,
    val status: CorridaStatus
)

// ═══════════════════════════════════════════════════════════════
// VIEWMODEL
// ═══════════════════════════════════════════════════════════════

@HiltViewModel
class GanhosViewModel @Inject constructor(
    private val entregadorRepository: EntregadorRepository,
    private val corridaRepository: CorridaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GanhosUiState())
    val uiState: StateFlow<GanhosUiState> = _uiState.asStateFlow()

    init {
        loadGanhos()
    }

    /**
     * Carrega os ganhos do entregador para o período selecionado.
     * TODO: Substituir por chamadas reais ao repositório.
     */
    fun loadGanhos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Descomentar quando backend estiver pronto
                // val ganhos = entregadorRepository.getGanhos(
                //     periodo = uiState.value.periodoSelecionado.name,
                //     dataInicio = null,
                //     dataFim = null
                // )

                // ── Scaffold/Placeholder — dados mock ────────
                val mockGanhos = generateMockGanhos()
                val mockCorridas = generateMockCorridasRealizadas()
                val totalKm = mockCorridas.sumOf { it.distanciaKm }
                val tempoOnline = 480 // 8 horas como scaffold
                val ganhoPorKm = if (totalKm > 0) mockGanhos.totalLiquido.toDouble() / totalKm else 0.0
                val ganhoPorHora = if (tempoOnline > 0) mockGanhos.totalLiquido.toDouble() / (tempoOnline / 60.0) else 0.0

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ganhos = mockGanhos,
                        corridasRealizadas = mockCorridas,
                        totalKmRodados = totalKm,
                        tempoOnlineMinutos = tempoOnline,
                        ganhoPorKm = ganhoPorKm,
                        ganhoPorHora = ganhoPorHora,
                        erro = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Erro ao carregar ganhos"
                    )
                }
            }
        }
    }

    /**
     * Altera o período e recarrega os dados.
     */
    fun selecionarPeriodo(periodo: PeriodoGanhos) {
        _uiState.update { it.copy(periodoSelecionado = periodo) }
        loadGanhos()
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }

    // ─── Mock data ──────────────────────────────────────────

    private fun generateMockGanhos(): Ganhos {
        return Ganhos(
            periodo = "Semanal",
            totalBruto = BigDecimal("847.50"),
            totalLiquido = BigDecimal("742.30"),
            totalCorridas = 38,
            mediaValorCorrida = BigDecimal("22.30"),
            corridasPorDia = mapOf(
                "2026-02-10" to GanhosDia("2026-02-10", BigDecimal("125.00"), 6),
                "2026-02-11" to GanhosDia("2026-02-11", BigDecimal("98.50"), 4),
                "2026-02-12" to GanhosDia("2026-02-12", BigDecimal("142.00"), 7),
                "2026-02-13" to GanhosDia("2026-02-13", BigDecimal("110.80"), 5),
                "2026-02-14" to GanhosDia("2026-02-14", BigDecimal("156.20"), 8),
                "2026-02-15" to GanhosDia("2026-02-15", BigDecimal("88.00"), 4),
                "2026-02-16" to GanhosDia("2026-02-16", BigDecimal("127.00"), 4)
            )
        )
    }

    private fun generateMockCorridasRealizadas(): List<CorridaRealizada> {
        return listOf(
            CorridaRealizada(
                id = "hist-001",
                clienteNome = "Restaurante Sabor & Arte",
                origemBairro = "Centro",
                destinoBairro = "Uvaranas",
                valor = 18.50,
                distanciaKm = 3.2,
                tempoMin = 12,
                dataHora = "16/02 · 14:32",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-002",
                clienteNome = "Padaria Grão Dourado",
                origemBairro = "Ronda",
                destinoBairro = "Estrela",
                valor = 12.00,
                distanciaKm = 1.8,
                tempoMin = 8,
                dataHora = "16/02 · 13:10",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-003",
                clienteNome = "Farmácia Saúde Total",
                origemBairro = "Centro",
                destinoBairro = "Nova Rússia",
                valor = 24.50,
                distanciaKm = 5.1,
                tempoMin = 20,
                dataHora = "16/02 · 11:45",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-004",
                clienteNome = "Açaí da Barra",
                origemBairro = "Centro",
                destinoBairro = "Oficinas",
                valor = 15.70,
                distanciaKm = 4.5,
                tempoMin = 18,
                dataHora = "16/02 · 10:20",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-005",
                clienteNome = "PetShop Amigo Fiel",
                origemBairro = "Uvaranas",
                destinoBairro = "Boa Vista",
                valor = 28.00,
                distanciaKm = 6.8,
                tempoMin = 25,
                dataHora = "15/02 · 19:50",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-006",
                clienteNome = "Lanchonete do Zé",
                origemBairro = "Centro",
                destinoBairro = "Cará-Cará",
                valor = 32.00,
                distanciaKm = 8.2,
                tempoMin = 30,
                dataHora = "15/02 · 18:15",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-007",
                clienteNome = "Pizzaria Bella Massa",
                origemBairro = "Estrela",
                destinoBairro = "Centro",
                valor = 9.80,
                distanciaKm = 1.2,
                tempoMin = 6,
                dataHora = "15/02 · 17:00",
                status = CorridaStatus.FINALIZADA
            ),
            CorridaRealizada(
                id = "hist-008",
                clienteNome = "Supermercado Condor",
                origemBairro = "Ronda",
                destinoBairro = "Uvaranas",
                valor = 19.30,
                distanciaKm = 4.0,
                tempoMin = 15,
                dataHora = "15/02 · 15:30",
                status = CorridaStatus.CANCELADA
            )
        )
    }
}
