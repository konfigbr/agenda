package osahner.dto

import osahner.domain.Medico

data class PacienteDto (
  var id: Long?=null,
  var user: UserDto?=null,
  var telefone: String?=null,
  var rg: String?=null,
  var cep: String?=null,
  var endereco: String?=null,
  var imagerg: String?=null,
  var cpf: String?=null,
  var imagecpf: String?=null,
  var ativo: String?,
  var profissional: MedicoDto?=null
)
