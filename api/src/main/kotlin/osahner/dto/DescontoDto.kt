package osahner.dto

import osahner.enums.TipoDesconto
import java.time.LocalDateTime

data class DescontoDto (
    var id: Long?,
    var tipo: TipoDesconto,
    var percentual: Int?,
    var ativo: Boolean?,
    var profissional: MedicoDto?,
    var dataInicio: LocalDateTime?,
    var dataFim: LocalDateTime?
)