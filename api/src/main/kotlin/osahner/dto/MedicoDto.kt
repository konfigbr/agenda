package osahner.dto

import osahner.enums.StatusMedico
import osahner.enums.StatusTermoDeUso
import osahner.enums.TipoDocumento
import java.time.LocalDateTime
import javax.persistence.Column

data class MedicoDto (
  var id: Long?,
  var user: UserDto?,
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
  var ativo :String?,
  var url :String?,
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
  var ramal: String?
)
