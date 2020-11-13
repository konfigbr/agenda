package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.AgendamentoStatus
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class HistoricoAgendamentoStatusDto(


  var id: Long?,

  var version: Long?,

  var dateCreated: LocalDateTime?,

  var userId: User?,

  var status: AgendamentoStatus?,

  var agendamentoId: Agendamento?,

  var observacao: String?








)
