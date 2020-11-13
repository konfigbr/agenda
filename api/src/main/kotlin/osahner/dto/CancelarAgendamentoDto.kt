package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.AgendamentoStatus
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import java.sql.Timestamp
import java.time.LocalDate

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class CancelarAgendamentoDto(

  var solicitacaoReserva: Long?,
  var agendamento: Long?,
  var carrinho: Long?


) {

}
