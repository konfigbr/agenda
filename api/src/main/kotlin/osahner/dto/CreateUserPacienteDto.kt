package osahner.dto

data class CreateUserPacienteDto (
  var id: Long?,
  var username: String?,
  var password: String?,
  var nome: String?,
  var telefone: String?,
  var rg: String?,
  var cep: String?,
  var endereco: String?,
  var imagerg: String?,
  var cpf: String?,
  var imagecpf: String?,
  var idProfissional: Long?
)
