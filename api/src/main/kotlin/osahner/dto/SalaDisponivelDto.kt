package osahner.dto

import osahner.domain.Assistente
import osahner.domain.Especialidade
import osahner.domain.Local
import osahner.domain.Sala
import osahner.enums.SalaStatus

data class SalaDisponivelDto (


  var sala: SalaDto?,

  var assistente: AssistenteCustomDto?


  )
