package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendaAssistenteDto(

  var id: Long?,

  var version: Long?,

  var groupId: String?,

  var diaDaSemana: Int?,

  var diasDaSemana: List<Int>?,

  var horaDoDia: Int?,

  var horaInicio: Int?,

  var horaFim: Int?,

  var assistenteEntity: Assistente?,

  var refSalaEntity: Sala?,

  var assistente: Long?,

  var sala: Long?







)
