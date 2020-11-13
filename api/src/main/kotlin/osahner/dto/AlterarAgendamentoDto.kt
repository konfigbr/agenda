package osahner.dto

import java.time.LocalDateTime

data class AlterarAgendamentoDto (
  var agendamento: Long? = null,

  var assistente: Long? = null,

  var horas: Long? = null

)

