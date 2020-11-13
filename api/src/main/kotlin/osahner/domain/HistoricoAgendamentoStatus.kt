package osahner.domain

import org.hibernate.annotations.Proxy
import osahner.dto.HistoricoAgendamentoStatusDto
import osahner.enums.AgendamentoStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "historico_agendamento_status")
@Proxy(lazy=false)
open class HistoricoAgendamentoStatus {
    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null



    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: AgendamentoStatus? = null

    @Column(name = "observacao", nullable = true)
    var observacao: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id", insertable = true, updatable = true)
    var refAgendamento: Agendamento? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id", insertable = true, updatable = true)
    var refUserEntity: User? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "dateCreated = $dateCreated " +

                    "status = $status " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HistoricoAgendamentoStatus

        if (id != other.id) return false
        if (version != other.version) return false
        if (dateCreated != other.dateCreated) return false

        if (status != other.status) return false

        return true
    }

  companion object {
    fun fromDTO(dto: HistoricoAgendamentoStatusDto): HistoricoAgendamentoStatus {
      var historico : HistoricoAgendamentoStatus = HistoricoAgendamentoStatus()

      historico.id = dto.id
      historico.version = dto.version
      historico.dateCreated = dto.dateCreated
      historico.refAgendamento = dto.agendamentoId
      historico.status = dto.status
   //   historico.refAgendamentoHistoricoAgendamentoStatusEntities = dto.refAgendamentoHistoricoAgendamentoStatusEntities
      historico.refUserEntity = dto.userId
      historico.observacao = dto.observacao



      return historico
    }
  }


  fun toDTO() = HistoricoAgendamentoStatusDto(
    id = this.id,
    version = this.version,
    dateCreated = this.dateCreated,
    status = this.status,
    userId = this.refUserEntity,
    agendamentoId = this.refAgendamento,
    observacao = this.observacao


  )

}

