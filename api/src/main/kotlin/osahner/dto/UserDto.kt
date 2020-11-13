package osahner.dto

data class UserDto (
  var id: Long?,
  var version: Long?,
  var passwordExpired: Boolean?,
  var passwordRecovered: Boolean?,
  var username: String?,
  var accountLocked: Boolean?,
  var password: String?,
  var nome: String?,
  var accountExpired: Boolean?,
  var enabled: Boolean?,
  var ativo: String?
)
