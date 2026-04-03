package com.pyloto.entregador.domain.model

object EnderecoMasking {

    fun aproximado(endereco: Endereco): String {
        val bairro = endereco.bairro.trim()
        if (bairro.isNotEmpty()) {
            return bairro
        }

        val streetName = endereco.logradouro.trim()
        if (streetName.isNotEmpty()) {
            return streetName
        }

        val cidade = endereco.cidade.trim()
        if (cidade.isNotEmpty()) {
            return cidade
        }

        return "Endereco indisponivel"
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
