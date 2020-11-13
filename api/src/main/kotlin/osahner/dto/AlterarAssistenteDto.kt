package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.Agendamento
import osahner.domain.Assistente

@JsonIgnoreProperties(ignoreUnknown = true)
data class AlterarAssistenteDto(
  var agendamento: Agendamento?,

  var assistente: Assistente?
)
