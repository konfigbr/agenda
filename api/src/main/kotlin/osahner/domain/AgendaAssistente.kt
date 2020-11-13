package osahner.domain

import osahner.dto.AgendaAssistenteDto
import osahner.dto.AgendamentoDto
import javax.persistence.*

@Entity
@Table(name = "agenda_assistente")
open class AgendaAssistente {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false,insertable = true)
    var version: Long? = 1

    @Column(name = "group_id", nullable = false)
    var groupId: String? = null

    @Column(name = "dia_da_semana", nullable = false)
    var diaDaSemana: Int? = null

    @Column(name = "hora_do_dia", nullable = false)
    var horaDoDia: Int? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assistente_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
    var assistente: Assistente? = null


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sala_id", referencedColumnName = "id", insertable = true, updatable = true, nullable =  false)
    var sala: Sala? = null

    @Transient
    var total: Long? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "groupId = $groupId " +
                    "diaDaSemana = $diaDaSemana " +

                    "horaDoDia = $horaDoDia " +

                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AgendaAssistente

        if (id != other.id) return false
        if (version != other.version) return false
        if (groupId != other.groupId) return false
        if (diaDaSemana != other.diaDaSemana) return false

        if (horaDoDia != other.horaDoDia) return false


        return true
    }

  companion object {
    fun fromDTO(dto: AgendaAssistenteDto): AgendaAssistente {
      var agendamento : AgendaAssistente = AgendaAssistente()

      agendamento.id = dto.id
      agendamento.version = dto.version
      agendamento.groupId = dto.groupId
      agendamento.diaDaSemana = dto.diaDaSemana
      agendamento.horaDoDia = dto.horaDoDia
      agendamento.assistente = dto.assistenteEntity
      agendamento.sala = dto.refSalaEntity

      return agendamento
    }
  }


  fun toDTO() = AgendaAssistenteDto(
    id = this.id,
    version = this.version,
    groupId = this.groupId,
    diaDaSemana =this.diaDaSemana,
    horaDoDia = this.horaDoDia,
    assistenteEntity = this.assistente,
    refSalaEntity = this.sala,
    diasDaSemana = null,
    horaInicio = null,
    horaFim = null,
    assistente = null,
    sala = null

  )






}

