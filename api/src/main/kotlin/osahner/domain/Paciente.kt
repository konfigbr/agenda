package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import osahner.dto.AssistenteCustomDto
import osahner.dto.AssistenteDto
import osahner.dto.PacienteCustomDto
import osahner.dto.PacienteDto
import javax.persistence.*

@Entity
@Table(name = "paciente")
class Paciente : PessoaAbstractJpa<Long>() {

  @Column(name = "ativo", nullable = false, columnDefinition = "varchar(1) default 'S'"  )
  var ativo: String? = null

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_profissional", referencedColumnName = "id")
  var profissional: Medico? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as Paciente

    if (ativo != other.ativo) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + (ativo?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "Paciente(ativo=$ativo)"
  }

  fun toDTO() = PacienteDto(
    id = this.id,
    user = this.user?.toDTO(),
    telefone = this.telefone,
    rg = this.rg,
    cep = this.cep,
    endereco = this.endereco,
    imagerg = this.imagerg,
    cpf = this.cpf,
    imagecpf = this.imagecpf,
    ativo = this.ativo,
    profissional = this.profissional?.toDTO()
  )

  companion object {
    fun fromDTO(dto: PacienteDto): Paciente {
      var paciente = Paciente()
      paciente.id = dto.id
      paciente.telefone = dto.telefone
      paciente.rg = dto.rg
      paciente.cep = dto.cep
      paciente.endereco = dto.endereco
      paciente.imagerg = dto.imagerg
      paciente.cpf = dto.cpf
      paciente.imagecpf = dto.imagecpf
      paciente.ativo = dto.ativo

      return paciente
    }

    fun fromCustomDTO(dto: PacienteCustomDto): Paciente {
      var paciente = Paciente()
      paciente.id = dto.id
      paciente.telefone = dto.telefone
      paciente.rg = dto.rg
      paciente.cep = dto.cep
      paciente.endereco = dto.endereco
      paciente.imagerg = dto.imagerg
      paciente.cpf = dto.cpf
      paciente.imagecpf = dto.imagecpf
      paciente.ativo = dto.ativo

      return paciente
    }
  }

  fun toCustomDTO() = PacienteCustomDto(
    id = this.id,
    idUser = this.user?.id,
    username = this.user?.username,
    nome = this.user?.nome,
    telefone = this.telefone,
    rg = this.rg,
    cep = this.cep,
    endereco = this.endereco,
    imagerg = this.imagerg,
    cpf = this.cpf,
    imagecpf = this.imagecpf,
    ativo = this.ativo,
    idProfissional = this.profissional?.id,
    profissional = this.profissional?.toCustomDTO()
  )
}
