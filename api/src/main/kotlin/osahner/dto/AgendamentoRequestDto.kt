package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.enums.PeriodoAgendamento
import osahner.enums.PeriodoReserva
import java.time.DayOfWeek
import java.time.LocalDateTime
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoRequestDto(
  var sala: Long?,

  var periodo: String?,

  var repeticaoPeriodo: Int?,

  var horaInicio: String?,

  var horaFim: String?,

  var medicoId: Long?,

  var dataInicio: String?,

  var dataFim: String?,

  var maximoAgendamentos: Int?,

  var diasSemana: Collection<Int>?,

  var periodoAgendamento : Int?
)

