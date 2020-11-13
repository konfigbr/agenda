package osahner.dto

import osahner.domain.FaixaPreco

data class TabelaPrecoDto(
    var id: Long? = null,

    var nome: String? = null,

    var faixaPrecos: MutableList<FaixaPreco?> = mutableListOf()
)

