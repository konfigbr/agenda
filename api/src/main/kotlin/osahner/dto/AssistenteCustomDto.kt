package osahner.dto

data class AssistenteCustomDto(
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
  var anoConclusao: String?,
  var faculdade: String?,
  var nomeDoCurso: String?,
  var ativo: String? = "S",
  var virtual: Boolean? = false,
  var manutencao: Boolean? = false
)
