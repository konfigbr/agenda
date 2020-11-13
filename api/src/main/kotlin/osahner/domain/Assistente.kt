package osahner.domain

import osahner.dto.AssistenteCustomDto
import osahner.dto.AssistenteDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "assistente")
class Assistente : PessoaAbstractJpa<Long>() {

  @Column(name = "faculdade", nullable = true)
  var faculdade: String? = null

  @Column(name = "ano_conclusao", nullable = true)
  var anoConclusao: String? = null

  @Column(name = "nome_do_curso", nullable = true)
  var nomeDoCurso: String? = null

  @Column(name = "ativo", nullable = false, columnDefinition = "varchar(1) default 'S'"  )
  var ativo: String? = null

  @Column(name = "`virtual`", nullable = false)
  var virtual: Boolean? = false

  @Column(name = "manutencao", nullable = false)
  var manutencao: Boolean? = false

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as Assistente

    if (faculdade != other.faculdade) return false
    if (anoConclusao != other.anoConclusao) return false
    if (nomeDoCurso != other.nomeDoCurso) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + (faculdade?.hashCode() ?: 0)
    result = 31 * result + (anoConclusao?.hashCode() ?: 0)
    result = 31 * result + (nomeDoCurso?.hashCode() ?: 0)
    return result
  }


  fun toDTO() = AssistenteDto(
    id = this.id,
    user = this.user?.toDTO(),
    telefone = this.telefone,
    rg = this.rg,
    cep = this.cep,
    endereco = this.endereco,
    imagerg = this.imagerg,
    cpf = this.cpf,
    imagecpf = this.imagecpf,
    anoConclusao = this.anoConclusao,
    faculdade = this.faculdade,
    nomeDoCurso = this.nomeDoCurso,
    ativo = this.ativo,
    virtual = this.virtual,
    manutencao = this.manutencao
    )

  companion object {
    fun fromDTO(dto: AssistenteDto): Assistente {
      var assistente : Assistente = Assistente()
      assistente.id = dto.id
      assistente.telefone = dto.telefone
      assistente.rg = dto.rg
      assistente.cep = dto.cep
      assistente.endereco = dto.endereco
      assistente.imagerg = dto.imagerg
      assistente.cpf = dto.cpf
      assistente.imagecpf = dto.imagecpf
      assistente.anoConclusao = dto.anoConclusao
      assistente.faculdade = dto.faculdade
      assistente.nomeDoCurso = dto.nomeDoCurso
      assistente.ativo = dto.ativo
      assistente.virtual = dto.virtual
      assistente.manutencao = dto.manutencao

      return assistente
    }

    fun fromCustomDTO(dto: AssistenteCustomDto): Assistente {
      var assistente : Assistente = Assistente()
      assistente.id = dto.id
      assistente.telefone = dto.telefone
      assistente.rg = dto.rg
      assistente.cep = dto.cep
      assistente.endereco = dto.endereco
      assistente.imagerg = dto.imagerg
      assistente.cpf = dto.cpf
      assistente.imagecpf = dto.imagecpf
      assistente.anoConclusao = dto.anoConclusao
      assistente.faculdade = dto.faculdade
      assistente.nomeDoCurso = dto.nomeDoCurso
      assistente.ativo = dto.ativo
      assistente.virtual = dto.virtual
      assistente.manutencao = dto.manutencao

      return assistente
    }
  }

  fun toCustomDTO() = AssistenteCustomDto(
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
    anoConclusao = this.anoConclusao,
    faculdade = this.faculdade,
    nomeDoCurso = this.nomeDoCurso,
    ativo = this.ativo,
    virtual = this.virtual,
    manutencao = this.manutencao
  )
}
