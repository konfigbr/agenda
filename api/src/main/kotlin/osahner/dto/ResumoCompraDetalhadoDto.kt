package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.Sala
import java.time.LocalDate
import java.time.LocalTime

@JsonIgnoreProperties("hibernateLazyInitializer")
data class ResumoCompraDetalhadoDto (
  var sala: Sala?,
  var data: LocalDate?,
  var horaInicio: LocalTime?,
  var horaFim: LocalTime?,
  var custo: Double?,
  var custoEquipamentos: Double?,
  var total: Double?,
  var nomeEquipamentos: String? = null
)
