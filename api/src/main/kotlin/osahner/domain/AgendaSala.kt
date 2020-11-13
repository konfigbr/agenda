package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import osahner.dto.AgendaSalaDto
import osahner.dto.AgendamentoDto
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "agenda_sala")
open class AgendaSala {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1

    @Column(name = "custo", nullable = false)
    var custo: Double? = null

    @Column(name = "inicio", nullable = false)
    var inicio: LocalDateTime? = null

    @Column(name = "fim", nullable = false)
    var fim: LocalDateTime? = null

    @Column(name = "fixo", nullable = false)
    var fixo: Boolean? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistente_id", referencedColumnName = "id", updatable = true)
    var assistente: Assistente? = null

    @ManyToOne
    @JoinColumn(name = "manutencao_id", referencedColumnName = "id")
    var manutencao: Manutencao? = null

    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id", nullable = true, insertable = true, updatable = true)
    var agendamento: Agendamento? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "sala_id", referencedColumnName = "id", insertable = true, updatable = true, nullable =  false)
    var sala: Sala? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "agendaSala",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var agendaSalaTipoEquipamentos: MutableSet<AgendaSalaTipoEquipamento>? = null

    override fun toString(): String {
      return "AgendaSala(id=$id, version=$version, custo=$custo, inicio=$inicio, fim=$fim, fixo=$fixo, assistente=$assistente, manutencao=$manutencao, agendamento=$agendamento, sala=$sala, agendaSalaTipoEquipamentos=$agendaSalaTipoEquipamentos)"
    }


    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AgendaSala

        if (id != other.id) return false
        if (version != other.version) return false
        if (custo != other.custo) return false

        if (inicio != other.inicio) return false
        if (fim != other.fim) return false
        if (fixo != other.fixo) return false

        return true
    }


  companion object {
    fun fromDTO(dto: AgendaSalaDto): AgendaSala {
      var agendamento : AgendaSala = AgendaSala()

      agendamento.id = dto.id
      agendamento.version = dto.version
      agendamento.custo = dto.custo
      agendamento.inicio = dto.inicio
      agendamento.fim = dto.fim
      agendamento.fixo = dto.fixo
      agendamento.assistente = dto.refUserEntity
      agendamento.manutencao = dto.refManutencaoEntity
      agendamento.agendamento = dto.refAgendamentoEntity
      agendamento.sala = dto.refSalaEntity
      agendamento.agendaSalaTipoEquipamentos = dto.refAgendaSalaTipoEquipamentoEntities


      return agendamento

    }
  }


  fun toDTO() = AgendaSalaDto(
    id = this.id,
    version = this.version,
    custo = this.custo,
    inicio = this.inicio,
    fim = this.fim,
    fixo = this.fixo,
    refUserEntity = this.assistente,
    refManutencaoEntity = this.manutencao,
    refAgendamentoEntity = this.agendamento,
    refSalaEntity = this.sala,
    refAgendaSalaTipoEquipamentoEntities = this.agendaSalaTipoEquipamentos

  )
}

