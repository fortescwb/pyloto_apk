package com.pyloto.entregador.domain.usecase.auth

import com.pyloto.entregador.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Verifica se o usuário está autenticado.
 * Usado pela MainActivity na inicialização para decidir
 * a tela de destino (Login vs Home).
 */
class CheckLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isLoggedIn()
    }
}
