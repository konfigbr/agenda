package osahner.domain

import osahner.dto.ConsultaDto
import osahner.enums.StatusConsulta
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "consulta")
open class Consulta {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    var refPacienteEntity: Paciente? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var refMedicoEntity: Medico? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id")
    var refAgendamentoEntity: Agendamento? = null

    @Column(name = "data_inicio", nullable = false)
    var dataInicio: LocalDateTime? = null

    @Column(name = "data_fim", nullable = false)
    var dataFim: LocalDateTime? = null

    @Column(name = "motivo", nullable = false)
    var motivo: String? = null

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: StatusConsulta? = StatusConsulta.SOLICITADA

    @Column(name = "observacao", nullable = true)
    var observacao: String? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    ")"

    fun toDTO(): ConsultaDto = ConsultaDto(
            this.id,
            this.version,
            this.refPacienteEntity?.toCustomDTO(),
            this.refMedicoEntity?.toCustomDTO(),
            this.dataInicio,
            this.dataFim,
            this.motivo,
            this.status,
            this.observacao
    )

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Consulta

        if (id != other.id) return false
        if (version != other.version) return false

        return true
    }

}

