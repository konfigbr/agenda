package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoAssistenteDto(

  var id: Long?,

  var inicio: String?,

  var fim: String?

  )
