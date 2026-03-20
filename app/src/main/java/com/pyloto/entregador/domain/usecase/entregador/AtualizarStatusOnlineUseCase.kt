package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class AtualizarStatusOnlineUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(online: Boolean) {
        repository.atualizarStatusOnline(online)
    }
}

