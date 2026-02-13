package com.pyloto.entregador.domain.model

data class Mensagem(
    val id: String,
    val corridaId: String,
    val remetenteId: String,
    val remetenteTipo: RemetenteTipo,
    val conteudo: String,
    val tipo: TipoMensagem,
    val timestamp: Long,
    val lida: Boolean
)

enum class RemetenteTipo {
    ENTREGADOR,
    CLIENTE,
    SISTEMA
}

enum class TipoMensagem {
    TEXTO,
    IMAGEM,
    LOCALIZACAO
}
