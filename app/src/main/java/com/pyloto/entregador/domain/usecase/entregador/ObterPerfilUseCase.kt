package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class ObterPerfilUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(): Entregador {
        return repository.getPerfil()
    }
}

