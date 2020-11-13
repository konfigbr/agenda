package osahner.dto

import java.time.LocalDateTime

data class AgendaSalaTipoEquipamentoDto (
  var id: Long? = null,

  var custo: Double? = null,

  var inicio: LocalDateTime? = null,

  var fim: LocalDateTime? = null,

  var tipoEquipamentoId: Long? = null,

  var nomeTipoEquipamento: String? = null
)

