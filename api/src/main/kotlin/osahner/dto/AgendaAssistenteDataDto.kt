package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
open class AgendaAssistenteDataDto {

  var groupId: String? = null

  var assistente: AssistenteCustomDto? = null

  var idSala: Long? = null

  var nomeSala: String? = null

  var horaInicio: Int? = null

  var horaFim: Int? = null

  var diaDaSemana : Int? = null
}
