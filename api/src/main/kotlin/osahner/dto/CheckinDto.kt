package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.StatusCheckin
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckinDto(

  var id: Long?,

  var status: StatusCheckin?,

  var horario: LocalDateTime?,

  var agendamentoId: Long?,

  var userId: Long?,

  var observacao: String?,

  var token: String?









)
