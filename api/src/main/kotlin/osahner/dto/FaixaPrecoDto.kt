package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.TabelaPreco
import osahner.enums.PeriodoAgendamento
import osahner.enums.PeriodoReserva
import java.time.DayOfWeek
import java.time.LocalDateTime
@JsonIgnoreProperties(ignoreUnknown = true)
data class FaixaPrecoDto(

  var id: Long? = null,

  var totalHoras: Int? = null,

  var valor: Double? = null,

  var tabelaPreco: TabelaPreco? = null
)

