package osahner.dto

import osahner.enums.TipoDocumento

class CreateUserDto(
  var accessType: String? = null,
  var username: String?,
  var password: String? = null,
  var nome: String?,
  var especialidade: Long? = null,
  var tipoDocumento: TipoDocumento? = null,
  var documento: String? = null
)
