package osahner.dto

import osahner.domain.Assistente
import osahner.domain.Especialidade
import osahner.domain.Local
import osahner.enums.SalaStatus

data class SalaDto (
  var id: Long?=null,

  var version: Long?=null,

  var status: SalaStatus?=null,

  var nome: String?=null,

  var local: LocalDto?=null,

  var especialidade : EspecialidadeDto?=null,

  var reserva : Int? = null,

  var assistenteVirtual : Assistente? = null
)
