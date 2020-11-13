package osahner.dto

import osahner.enums.StatusFatura
import java.time.LocalDate
import java.time.LocalDateTime

data class FaturaDto (
  var id: Long?,
  var version: Long?,
  var valor: Double?,
  var dateCreated: LocalDateTime?,
  var lastUpdated: LocalDateTime?,
  var dataCobranca: LocalDate?,
  var uuidTransacao: String?,
  var status: StatusFatura?,
  var carrinhoCompra: CarrinhoCompraDto?,
  var medico:  MedicoDto?,
  var idPagamento: String?
)
