package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.StatusMensagemTipo
import osahner.enums.TipoMensagem
import java.sql.Timestamp

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

data class MensagemDto (
  var id: Long? = null,

  var version: Long? = null,

  var dateCreated: LocalDateTime? = null,

  var status: StatusMensagemTipo? = null,

  var tipo: TipoMensagem? = null,

  var mensagem: String? = null,

  var remetente: UserDto? = null,

  var sala: SalaDto? = null,

  var destinatario: UserDto? = null
)
