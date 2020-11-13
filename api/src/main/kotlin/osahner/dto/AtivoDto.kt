package osahner.dto

data class AtivoDto (
  var id: Long?,
  var version: Long?,
  var valorResidual: Double?,
  //var valorCobravel: Double?,
  var custo: Double?,
  var dataEntrada:  java.sql.Timestamp?,
  var nome: String?,
  var cobravel: java.lang.Boolean?,
  var depreciacao: Double?,
  var tipoEquipamento: TipoEquipamentoDto?
)
