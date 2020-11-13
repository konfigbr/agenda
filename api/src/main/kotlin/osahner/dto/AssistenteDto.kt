package osahner.dto

data class AssistenteDto(
  var id: Long?=null,
  var user: UserDto?=null,
  var telefone: String?=null,
  var rg: String?=null,
  var cep: String?=null,
  var endereco: String?=null,
  var imagerg: String?=null,
  var cpf: String?=null,
  var imagecpf: String?=null,
  var anoConclusao: String?=null,
  var faculdade: String?=null,
  var nomeDoCurso: String?=null,
  var ativo: String?,
  var virtual: Boolean? = false,
  var manutencao: Boolean? = false
)
