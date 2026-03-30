package com.pyloto.entregador.domain.model

object EnderecoMasking {

    fun aproximado(endereco: Endereco): String {
        return "${endereco.bairro}, ${endereco.cidade}"
    }

    fun exibirEndereco(endereco: Endereco, corridaAceita: Boolean): String {
        return if (corridaAceita) {
            endereco.enderecoFormatado
        } else {
            aproximado(endereco)
        }
    }

    fun isCorridaAceita(status: CorridaStatus): Boolean {
        return status != CorridaStatus.DISPONIVEL
    }
}
