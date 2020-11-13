package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendarAssistenteDto(


  var groupId: String?,

  var diasDaSemana: List<Int>?,

  var diaDaSemana: Int?,

  var horaDoDia: Int?,


  var horaInicio: Int?,

  var horaFim: Int?,

  var assistente: Long?,

  var sala: Long?






)
