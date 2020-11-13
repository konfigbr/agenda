package osahner.dto

import osahner.domain.FaixaPreco
import osahner.enums.TipoTermoDeUso

data class TermoDeUsoDto(
    var id: Long? = null,

    var tipo: TipoTermoDeUso? = null,

    var texto: String? = null
)

