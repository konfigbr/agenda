package osahner.dto

import osahner.enums.StatusMedico
import osahner.enums.StatusTermoDeUso
import osahner.enums.TipoDocumento
import java.time.LocalDateTime

data class MedicoCustomDto (
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
  var crm: String?,
  var ufcrm: String?,
  var imagecrm: String?,
  var especialidade: EspecialidadeDto?,
  var status: StatusMedico?,
  var imageFoto: String?,
  var imageComprovante: String?,
  var termoAceite: String?,
  var ativo: String?,
  var url: String?,
  var tipoDocumento: TipoDocumento?,
  var categoria: String?,
  var termoUsoPrivacidade: StatusTermoDeUso?,
  var numero: String?,
  var logradouro: String?,
  var cidade: String?,
  var bairro: String?,
  var nacionalidade: String?,
  var estadoCivil: String?,
  var uf: String?,
  var dateCreated: LocalDateTime?,
  var hasDescontoVigente: Boolean?,
  var ramal: String?
)
