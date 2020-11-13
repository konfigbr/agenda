package osahner.domain

import osahner.dto.FaturaDto
import osahner.dto.MensagemDto
import osahner.dto.SalaDto
import osahner.enums.StatusMensagemTipo
import osahner.enums.TipoMensagem
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "mensagem")
open class Mensagem {
  @Id
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column(name = "date_created", nullable = false)
  var dateCreated: LocalDateTime? = null

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  var status: StatusMensagemTipo? = null

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false)
  var tipo: TipoMensagem? = null

  @Column(name = "mensagem", nullable = false)
  var mensagem: String? = null

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "remetente_id", referencedColumnName = "id")
  var remetente: User? = null
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sala_id", referencedColumnName = "id")
  var sala: Sala? = null
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "destinatario_id", referencedColumnName = "id")
  var destinatario: User? = null
  @OneToMany(mappedBy = "refMensagemEntity")
  var refMensagemRoleEntities: MutableList<MensagemRole>? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Mensagem

    if (id != other.id) return false
    if (dateCreated != other.dateCreated) return false
    if (status != other.status) return false
    if (tipo != other.tipo) return false
    if (mensagem != other.mensagem) return false
    if (remetente != other.remetente) return false
    if (sala != other.sala) return false
    if (destinatario != other.destinatario) return false
    if (refMensagemRoleEntities != other.refMensagemRoleEntities) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (dateCreated?.hashCode() ?: 0)
    result = 31 * result + (status?.hashCode() ?: 0)
    result = 31 * result + (tipo?.hashCode() ?: 0)
    result = 31 * result + (mensagem?.hashCode() ?: 0)
    result = 31 * result + (remetente?.hashCode() ?: 0)
    result = 31 * result + (sala?.hashCode() ?: 0)
    result = 31 * result + (destinatario?.hashCode() ?: 0)
    result = 31 * result + (refMensagemRoleEntities?.hashCode() ?: 0)
    return result
  }

  override fun toString(): String {
    return "Mensagem(id=$id, dateCreated=$dateCreated, status=$status, tipo=$tipo, mensagem=$mensagem, remetente=$remetente, sala=$sala, destinatario=$destinatario, refMensagemRoleEntities=$refMensagemRoleEntities)"
  }

  fun toDTO() = MensagemDto(
      id = this.id,
      destinatario = this.destinatario?.toDTO(),
      mensagem = this.mensagem,
      remetente = this.remetente?.toDTO(),
      sala = this.sala?.toDTO(),
      tipo = this.tipo,
      status = this.status,
      dateCreated = this.dateCreated
    )

  companion object {
    fun fromDTO(dto: MensagemDto): Mensagem {
      var mensagem = Mensagem()

      mensagem.id = dto.id
      mensagem.destinatario = dto.destinatario?.let { User.fromDTO(it) }
      mensagem.mensagem = dto.mensagem
      mensagem.remetente = dto.remetente?.let { User.fromDTO(it) }
      mensagem.sala = dto.sala?.let { Sala.fromDTO(it) }
      mensagem.tipo = dto.tipo
      mensagem.status = dto.status
      mensagem.dateCreated = dto.dateCreated

      return mensagem
    }
  }

}

