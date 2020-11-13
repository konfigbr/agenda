package osahner.dto

import osahner.enums.StatusConsulta

data class TrocaStatusConsultaDto (
    var status: StatusConsulta?,
    var observacao: String? = null
)
