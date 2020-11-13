package osahner.dto

import osahner.domain.FaixaPreco
import osahner.enums.TipoTermoDeUso

data class ParametroDto(
    var id: Long? = null,

    var tipo: String? = null,

    var valor: String? = null
)

