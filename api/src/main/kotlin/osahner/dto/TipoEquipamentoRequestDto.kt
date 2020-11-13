package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.enums.PeriodoAgendamento
import osahner.enums.PeriodoReserva
import java.time.DayOfWeek
import java.time.LocalDateTime
@JsonIgnoreProperties(ignoreUnknown = true)
data class TipoEquipamentoRequestDto(
  var tipoEquipamento: Long?,

  var inicio: String?,

  var fim: String?

)

