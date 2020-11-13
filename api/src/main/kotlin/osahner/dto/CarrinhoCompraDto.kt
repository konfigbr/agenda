package osahner.dto

import osahner.enums.SolicitacaoStatus
import java.time.LocalDateTime

data class CarrinhoCompraDto (
  var id: Long?,
  var valor: Double?,
  var dateCreated: LocalDateTime?,
  var custo: Double?,
  var lastUpdated: LocalDateTime?,
  var desconto: Double?,
  var status: SolicitacaoStatus?,
  var createdBy: MedicoDto?,
  var usuario: UserDto?
)
