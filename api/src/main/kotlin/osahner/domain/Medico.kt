package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.MedicoCustomDto
import osahner.dto.MedicoDto
import osahner.enums.StatusMedico
import osahner.enums.StatusTermoDeUso
import osahner.enums.TipoDocumento
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "medico")
@JsonIgnoreProperties(ignoreUnknown = true)
class Medico : PessoaAbstractJpa<Long>() {

  @Column(name = "status", nullable = true)
  @Enumerated(EnumType.STRING)
  var status: StatusMedico? = null

  @Column(name = "crm", nullable = true)
  var crm: String? = null

  @Column(name = "ufcrm", nullable = true)
  var ufcrm: String? = null

  @Column(name = "imagecrm", nullable = true)
  var imagecrm: String? = null

  @Column(name = "imag_foto", nullable = true)
  var imageFoto: String? = null

  @Column(name = "imag_comprovante", nullable = true)
  var imageComprovante: String? = null

  @Column(name = "termo_aceite", nullable = true)
  var termoAceite: String? = null

  @Column(name = "ativo", nullable = false, columnDefinition = "varchar(1) default 'S'"  )
  var ativo: String? = null

  @Column(name = "numero", nullable = true)
  var numero: String? = null

  @Column(name = "logradouro", nullable = true)
  var logradouro: String? = null

  @Column(name = "cidade", nullable = true)
  var cidade: String? = null

  @Column(name = "bairro", nullable = true)
  var bairro: String? = null

  @Column(name = "nacionalidade", nullable = true)
  var nacionalidade: String? = null

  @Column(name = "estado_civil", nullable = true)
  var estadoCivil: String? = null

  @Column(name = "uf", nullable = true)
  var uf: String? = null

  @Enumerated(EnumType.STRING)
  @Column(name = "termo_uso_privacidade", nullable = true)
  var termoUsoPrivacidade: StatusTermoDeUso? = null

  @Column(name = "url", nullable = true)
  var url: String? = null

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_documento", nullable = true)
  var tipoDocumento: TipoDocumento? = null

  @Column(name = "date_created", nullable = true)
  var dateCreated: LocalDateTime? = null

  @get:JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "especialidade_id", referencedColumnName = "id")
  var refEspecialidadeEntity: Especialidade? = null

  fun toDTO() = MedicoDto(
    id = this.id,
    user = this.user?.toDTO(),
    telefone = this.telefone,
    rg = this.rg,
    cep = this.cep,
    endereco = this.endereco,
    imagerg = this.imagerg,
    cpf = this.cpf,
    imagecpf = this.imagecpf,
    crm = this.crm,
    ufcrm = this.ufcrm,
    imagecrm = this.imagecrm,
    especialidade = this.refEspecialidadeEntity?.toDTO(),
    status = this.status,
    imageFoto = this.imageFoto,
    imageComprovante = this.imageComprovante,
    termoAceite = this.termoAceite,
    ativo = this.ativo,
    url = this.url,
    tipoDocumento = this.tipoDocumento,
    termoUsoPrivacidade = this.termoUsoPrivacidade,
    numero = this.numero,
    logradouro = this.logradouro,
    cidade = this.cidade,
    bairro = this.bairro,
    nacionalidade = this.nacionalidade,
    estadoCivil = this.estadoCivil,
    uf = this.uf,
    dateCreated = this.dateCreated
  )

  companion object {
    fun fromDTO(dto: MedicoDto): Medico {
      var medico = Medico()

      medico.id = dto.id
      medico.telefone = dto.telefone
      medico.rg = dto.rg
      medico.cep = dto.cep
      medico.endereco = dto.endereco
      medico.imagerg = dto.imagerg
      medico.cpf = dto.cpf
      medico.imagecpf = dto.imagecpf
      medico.crm = dto.crm
      medico.ufcrm = dto.ufcrm
      medico.imagecrm = dto.imagecrm
      medico.status = dto.status!!
      medico.imageFoto = dto.imageFoto
      medico.termoAceite = dto.termoAceite
      medico.imageComprovante = dto.imageComprovante
      medico.ativo = dto.ativo
      medico.url = dto.url
      medico.tipoDocumento = dto.tipoDocumento
      medico.termoUsoPrivacidade = dto.termoUsoPrivacidade
      medico.numero = dto.numero
      medico.logradouro = dto.logradouro
      medico.cidade = dto.cidade
      medico.bairro = dto.bairro
      medico.nacionalidade = dto.nacionalidade
      medico.estadoCivil = dto.estadoCivil
      medico.uf = dto.uf
      medico.dateCreated = dto.dateCreated

      return medico
    }

    fun fromCustomDTO(dto: MedicoCustomDto): Medico {
      var medico : Medico = Medico()

      medico.id = dto.id
      medico.telefone = dto.telefone
      medico.rg = dto.rg
      medico.cep = dto.cep
      medico.endereco = dto.endereco
      medico.imagerg = dto.imagerg
      medico.cpf = dto.cpf
      medico.imagecpf = dto.imagecpf
      medico.crm = dto.crm
      medico.ufcrm = dto.ufcrm
      medico.imagecrm = dto.imagecrm
      medico.status = dto.status
      medico.imageFoto = dto.imageFoto
      medico.imageComprovante = dto.imageComprovante
      medico.termoAceite = dto.termoAceite
      medico.ativo = dto.ativo
      medico.url = dto.url
      medico.tipoDocumento = dto.tipoDocumento
      medico.termoUsoPrivacidade = dto.termoUsoPrivacidade
      medico.numero = dto.numero
      medico.logradouro = dto.logradouro
      medico.cidade = dto.cidade
      medico.bairro = dto.bairro
      medico.nacionalidade = dto.nacionalidade
      medico.estadoCivil = dto.estadoCivil
      medico.uf = dto.uf
      medico.dateCreated = dto.dateCreated


      return medico
    }
  }

  fun toCustomDTO() = MedicoCustomDto(
    id = this.id,
    username = this.user?.username,
    idUser = this.user?.id,
    nome = this.user?.nome,
    telefone = this.telefone,
    rg = this.rg,
    cep = this.cep,
    endereco = this.endereco,
    imagerg = this.imagerg,
    cpf = this.cpf,
    imagecpf = this.imagecpf,
    crm = this.crm,
    ufcrm = this.ufcrm,
    imagecrm = this.imagecrm,
    especialidade = this.refEspecialidadeEntity?.toDTO(),
    status = this.status,
    imageFoto = this.imageFoto,
    termoAceite = this.termoAceite,
    imageComprovante = this.imageComprovante,
    ativo = this.ativo,
    url = this.url,
    tipoDocumento = this.tipoDocumento,
    termoUsoPrivacidade = this.termoUsoPrivacidade,
    numero = this.numero,
    logradouro = this.logradouro,
    cidade = this.cidade,
    bairro = this.bairro,
    nacionalidade = this.nacionalidade,
    estadoCivil = this.estadoCivil,
    uf = this.uf,
    dateCreated = this.dateCreated,
    hasDescontoVigente = null
  )
}
