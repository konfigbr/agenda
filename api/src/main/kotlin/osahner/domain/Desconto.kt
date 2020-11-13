package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.DescontoDto
import osahner.dto.EspecialidadeDto
import osahner.dto.SalaDto
import osahner.enums.StatusConsulta
import osahner.enums.StatusFatura
import osahner.enums.TipoAceiteEspecialidade
import osahner.enums.TipoDesconto
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "desconto")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Desconto {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    var tipo: TipoDesconto? = null

    @Column(name = "percentual", nullable = false)
    var percentual: Int? = null

    @Column(name = "ativo", nullable = false)
    var ativo: Boolean? = true

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var refMedicoEntity: Medico? = null

    @Column(name = "data_inicio", nullable = false)
    var dataInicio: LocalDateTime? = null

    @Column(name = "data_fim", nullable = false)
    var dataFim: LocalDateTime? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Desconto

        if (id != other.id) return false
        if (tipo != other.tipo) return false
        if (percentual != other.percentual) return false
        if (ativo != other.ativo) return false
        if (refMedicoEntity != other.refMedicoEntity) return false
        if (dataInicio != other.dataInicio) return false
        if (dataFim != other.dataFim) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (tipo?.hashCode() ?: 0)
        result = 31 * result + (percentual ?: 0)
        result = 31 * result + (ativo?.hashCode() ?: 0)
        result = 31 * result + (refMedicoEntity?.hashCode() ?: 0)
        result = 31 * result + (dataInicio?.hashCode() ?: 0)
        result = 31 * result + (dataFim?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Desconto(id=$id, tipo=$tipo, percentual=$percentual, ativo=$ativo, refMedicoEntity=$refMedicoEntity, dataInicio=$dataInicio, dataFim=$dataFim)"
    }

    companion object {
        fun fromDTO(dto: DescontoDto): Desconto {
            var desconto = Desconto()

            desconto.id = dto.id
            desconto.tipo = dto.tipo
            desconto.percentual = dto.percentual
            desconto.ativo = dto.ativo
            desconto.refMedicoEntity = Medico.fromDTO(dto.profissional!!)
            desconto.dataInicio = dto.dataInicio
            desconto.dataFim = dto.dataFim

            return desconto
        }
    }

    fun toDTO() = DescontoDto(
        id = this.id,
        percentual = this.percentual,
        ativo = this.ativo,
        tipo = this.tipo!!,
        profissional = this.refMedicoEntity?.toDTO(),
        dataInicio = this.dataInicio,
        dataFim = this.dataFim
    )
}

