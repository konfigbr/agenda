package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.AgendaSalaTipoEquipamentoDto
import osahner.enums.SolicitacaoStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "agenda_sala_tipo_equipamento")
@JsonIgnoreProperties(ignoreUnknown = true)
open class AgendaSalaTipoEquipamento {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    //@Column(name = "uuid", nullable = false, insertable = false, updatable = false)
    @Transient
    var uuid: Long? = null


  @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SolicitacaoStatus? = null

    @Column(name = "valor", nullable = false)
    var valor: Double? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_equipamento_id", referencedColumnName = "id")
    var tipoEquipamento: TipoEquipamento? = null
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agenda_sala_id", referencedColumnName = "id")
    var agendaSala: AgendaSala? = null
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitacao_equipamento_id", referencedColumnName = "id")
    var solicitacaoEquipamento: SolicitacaoEquipamento? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fatura_id", referencedColumnName = "id")
    var fatura: Fatura? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "agendaSalaTipoEquipamento")
    var creditoAgendaSalaTipoEquipamento: MutableList<CreditoAgendaSalaTipoEquipamento>? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "dateCreated = $dateCreated " +
                    "status = $status " +
                    "valor = $valor " +
                    "version = $version " +
                    ")"

  fun toCustomDto(): AgendaSalaTipoEquipamentoDto {
    return AgendaSalaTipoEquipamentoDto(
      id = id,
      custo = agendaSala?.custo,
      inicio = agendaSala?.inicio,
      fim = agendaSala?.fim,
      tipoEquipamentoId = tipoEquipamento?.id,
      nomeTipoEquipamento = tipoEquipamento?.nome
    )
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AgendaSalaTipoEquipamento

    if (id != other.id) return false
    if (uuid != other.uuid) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (uuid?.hashCode() ?: 0)
    return result
  }

}

