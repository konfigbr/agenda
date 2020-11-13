package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import osahner.dto.AgendamentoDto
import osahner.enums.AgendamentoStatus
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva


import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "agendamento")
@JsonIgnoreProperties(ignoreUnknown = true)
@Proxy(lazy=false)
open class Agendamento {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1

    @Column(name = "dia", nullable = false)
    var dia:  LocalDate? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Column(name = "custo", nullable = false)
    var custo: Double = 0.0

    @Column(name = "agendamento_status_atual", nullable = false)
    @Enumerated(EnumType.STRING)
    var agendamentoStatusAtual: AgendamentoStatus? = AgendamentoStatus.NAO_INICIADO

    @Column(name = "last_updated", nullable = true)
    var lastUpdated: LocalDateTime? = null

   @Column(name = "status", nullable = false)
   @Enumerated(EnumType.STRING)
   var status: SolicitacaoStatus? = null

  @Column(name = "alterou", nullable = true)
  var alterou: Boolean? = false

  @Column(name = "periodo", nullable = false)
  @Enumerated(EnumType.STRING)
  var periodo: PeriodoAgendamento? = null

    @OneToMany(mappedBy = "agendamento",cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    var agendaSalas: MutableList<AgendaSala>? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitacao_reserva_id", referencedColumnName = "id", insertable = true, updatable = false)
    var solicitacaoReserva: SolicitacaoReserva? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id", insertable = true, updatable = true)
    var medico : Medico? = null


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fatura_id", referencedColumnName = "id", insertable = true, updatable = true)
    var refFaturaEntity: Fatura? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "refAgendamento")
    var agendamentoHistoricoAgendamentoStatus: MutableList<AgendamentoHistoricoAgendamentoStatus>? = null

  /* @get:JsonIgnore
    @OneToMany(mappedBy = "refAgendamento")
    var historicoAgendamentoStatus: MutableList<HistoricoAgendamentoStatus>? = null
*/
    @get:JsonIgnore
    @OneToMany(mappedBy = "refAgendamentoEntity")
    var refConsultaEntities: MutableList<Consulta>? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "agendamento")
    var creditos: MutableList<Credito>? = null

    @get:JsonIgnore
    @OneToOne
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id", nullable = true, insertable = true, updatable = true)
    var agendamentoPai: Agendamento? = null

    @Column(name = "tipoReserva", nullable = false)
    @Enumerated(EnumType.STRING)
    var tipoReserva: TipoReserva? = TipoReserva.USO

  override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "dia = $dia " +
                    "dateCreated = $dateCreated " +
                    "custo = $custo " +
                    "agendamentoStatusAtual = $agendamentoStatusAtual " +
                    "lastUpdated = $lastUpdated " +
                    "solicitacaoReservaId = $solicitacaoReserva " +
                    "medicoId = $medico " +
                    "status = $status " +
                    "periodo = $periodo " +
                    ")"

  companion object {
    fun fromDTO(dto: AgendamentoDto): Agendamento {
      var agendamento = Agendamento()

      agendamento.id = dto.id
      agendamento.version = dto.version
      agendamento.dia = dto.dia
      agendamento.dateCreated = dto.dateCreated
      agendamento.custo = dto.custo
      agendamento.agendamentoStatusAtual = dto.agendamentoStatusAtual
      agendamento.lastUpdated = dto.lastUpdated
      agendamento.status = dto.status
      agendamento.periodo = dto.periodo
      agendamento.medico = dto.user
      agendamento.alterou = dto.alterou
      agendamento.tipoReserva = dto.tipoReserva

      return agendamento
    }
  }


  fun toDTO() = AgendamentoDto(
    id = this.id,
    version = this.version,
    dia = this.dia,
    dateCreated =this.dateCreated,
    custo = this.custo,
    agendamentoStatusAtual = this.agendamentoStatusAtual,
    lastUpdated = this.lastUpdated,
    status = this.status,
    periodo = this.periodo,
    user = this.medico,
    sala = null,
    assistente = null,
    inicio = null,
    fim=null,
    horarioSalaOcupada=null,
    horarioSalaLiberada=null,
    custoHora=null,
    tiposEquipamentos= mutableListOf(),
    alterou = this.alterou,
    tipoReserva = this.tipoReserva
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Agendamento

    if (id != other.id) return false
    if (dia != other.dia) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (dia?.hashCode() ?: 0)
    return result
  }
}

