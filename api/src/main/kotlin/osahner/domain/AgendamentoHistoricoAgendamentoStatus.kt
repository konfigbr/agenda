package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import javax.persistence.*

@Entity
@Table(name = "agendamento_historico_agendamento_status")
@JsonIgnoreProperties(ignoreUnknown = true)
@Proxy(lazy=false)
open class AgendamentoHistoricoAgendamentoStatus {

  @Id
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column(name = "agendamento_historico_agendamento_status_id", nullable = false, insertable = false, updatable = false)
    var agendamentoHistoricoAgendamentoStatusId: Long? = null

    @Column(name = "historico_agendamento_status_id", nullable = true, insertable = false, updatable = false)
    var historicoAgendamentoStatusId: Long? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_historico_agendamento_status_id", referencedColumnName = "id")
    var refAgendamento: Agendamento? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "historico_agendamento_status_id", referencedColumnName = "id")
    var refHistoricoAgendamentoStatusEntity: HistoricoAgendamentoStatus? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "agendamentoHistoricoAgendamentoStatusId = $agendamentoHistoricoAgendamentoStatusId " +
                    "historicoAgendamentoStatusId = $historicoAgendamentoStatusId " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AgendamentoHistoricoAgendamentoStatus

        if (agendamentoHistoricoAgendamentoStatusId != other.agendamentoHistoricoAgendamentoStatusId) return false
        if (historicoAgendamentoStatusId != other.historicoAgendamentoStatusId) return false

        return true
    }

}

