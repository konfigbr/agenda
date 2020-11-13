package osahner.dto

import osahner.domain.Medico

data class PacienteCustomDto(
  var id: Long?,
  var idUser: Long?,
  var username: String?,
  var nome: String?,
  var telefone: String?,
  var rg: String?,
  var cep: String?,
  var endereco: String?,
  var imagerg: String?,
  var cpf: String?,
  var imagecpf: String?,
  var ativo: String? = "S",
  var idProfissional: Long?,
  var profissional: MedicoCustomDto?
)
