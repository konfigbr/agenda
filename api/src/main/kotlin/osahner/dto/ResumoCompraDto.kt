package osahner.dto

import java.time.LocalTime

open class ResumoCompraDto {
  var valor: Double? = null
  var desconto: Double? = null
  var horaInicio: LocalTime? = null
  var resumosMensais: MutableList<ResumoMensalDto> = mutableListOf()
}
