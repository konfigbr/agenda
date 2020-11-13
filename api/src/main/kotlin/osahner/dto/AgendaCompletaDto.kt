package osahner.dto

import java.time.LocalDateTime

data class AgendaCompletaDto (
  var groupId: String? = null,
  var assistente: AgendaCompletaAssistenteDto? = null,
  var sala: AgendaCompletaSalaDto?=null,
  var horaInicio: Int?=null,
  var horaFim: Int?=null,
  var diaDaSemana: Int?=null
)

data class AgendaCompletaSalaDto (
  var id: Long? = null,
  var nome: String? = null
)

data class AgendaCompletaAssistenteDto (
  var id: Long? = null,
  var nome: String? = null,
  var horaInicio: Int? = null,
  var horaFim: Int? =  null,
  var diaDaSemana: Int? = null
)

data class AgendaCompletaSalaReturnDto (
  var agendamentos: MutableList<AgendamentoCustomDto>? = null,
  var agendaAssistente: MutableList<AgendaCompletaDto>? = null

)


data class AgendaCheckinDto (
  var assistantCheckinHour: LocalDateTime? = null,
  var assistantCheckoutHour: LocalDateTime? = null,
  var professionalCheckinHour: LocalDateTime? = null,
  var professionalCheckoutHour: LocalDateTime? = null
)


