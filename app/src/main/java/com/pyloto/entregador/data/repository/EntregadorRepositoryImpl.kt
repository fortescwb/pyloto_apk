package com.pyloto.entregador.data.repository

import com.pyloto.entregador.core.database.dao.EntregadorDao
import com.pyloto.entregador.core.database.entity.EntregadorEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.data.remote.model.AtualizarPerfilRequest
import com.pyloto.entregador.data.remote.model.StatusRequest
import com.pyloto.entregador.domain.model.*
import com.pyloto.entregador.domain.repository.EntregadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntregadorRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val entregadorDao: EntregadorDao
) : EntregadorRepository {

    override suspend fun getPerfil(): Entregador {
        return try {
            val response = apiService.getPerfil()
            val perfil = response.data
            val entity = EntregadorEntity(
                id = perfil.id,
                nome = perfil.nome,
                email = perfil.email,
                telefone = perfil.telefone,
                cpf = perfil.cpf,
                fotoUrl = perfil.fotoUrl,
                veiculoTipo = perfil.veiculoTipo,
                veiculoPlaca = perfil.veiculoPlaca,
                rating = perfil.rating,
                totalCorridas = perfil.totalCorridas,
                statusOnline = perfil.statusOnline
            )
            entregadorDao.insert(entity)
            entity.toDomain()
        } catch (e: Exception) {
            val cached = entregadorDao.getEntregador() ?: throw e
            cached.toDomain()
        }
    }

    override fun observarPerfil(): Flow<Entregador?> {
        return entregadorDao.observarEntregador().map { it?.toDomain() }
    }

    override suspend fun atualizarPerfil(entregador: Entregador): Entregador {
        val request = AtualizarPerfilRequest(
            nome = entregador.nome,
            telefone = entregador.telefone,
            fotoUrl = null,
            veiculoTipo = entregador.veiculo?.tipo?.name,
            veiculoPlaca = entregador.veiculo?.placa
        )
        val response = apiService.atualizarPerfil(request)
        return getPerfil()
    }

    override suspend fun atualizarStatusOnline(online: Boolean) {
        apiService.atualizarStatus(StatusRequest(disponivel = online))
        entregadorDao.atualizarStatusOnline(online)
    }

    override suspend fun getGanhos(periodo: String, dataInicio: String?, dataFim: String?): Ganhos {
        val response = apiService.getGanhos(periodo, dataInicio, dataFim)
        val data = response.data
        return Ganhos(
            periodo = data.periodo,
            totalBruto = BigDecimal.valueOf(data.totalBruto),
            totalLiquido = BigDecimal.valueOf(data.totalLiquido),
            totalCorridas = data.totalCorridas,
            mediaValorCorrida = BigDecimal.valueOf(data.mediaValorCorrida),
            corridasPorDia = data.corridasPorDia.mapValues { (_, v) ->
                GanhosDia(
                    data = v.data,
                    valor = BigDecimal.valueOf(v.valor),
                    quantidadeCorridas = v.quantidadeCorridas
                )
            }
        )
    }

    private fun EntregadorEntity.toDomain(): Entregador {
        return Entregador(
            id = this.id,
            nome = this.nome,
            email = this.email,
            telefone = this.telefone,
            cpf = this.cpf,
            fotoUrl = this.fotoUrl,
            veiculo = this.veiculoTipo?.let {
                Veiculo(
                    tipo = VeiculoTipo.valueOf(it),
                    placa = this.veiculoPlaca
                )
            },
            rating = this.rating,
            totalCorridas = this.totalCorridas,
            statusOnline = this.statusOnline
        )
    }
}
