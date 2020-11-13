package osahner.dto

import osahner.domain.Assistente
import osahner.domain.Sala
import java.time.LocalDateTime

data class AgendaAssistenteRequestDto (
  var sala: Sala?,
  var inicio: LocalDateTime?,
  var fim: LocalDateTime?,
  var assistente: Assistente?
)
