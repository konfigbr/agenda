package osahner.domain

import osahner.dto.TermoDeUsoDto
import osahner.enums.TipoTermoDeUso
import javax.persistence.*

@Entity
@Table(name = "termo_de_uso")
open class TermoDeUso {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    var tipo: TipoTermoDeUso? = null

    @Column(name = "texto", nullable = false)
    var texto: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TermoDeUso

        if (id != other.id) return false
        if (tipo != other.tipo) return false
        if (texto != other.texto) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (tipo?.hashCode() ?: 0)
        result = 31 * result + (texto?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TermoDeUso(id=$id, tipo=$tipo, texto=$texto)"
    }

    companion object {
        fun fromDTO(dto: TermoDeUsoDto): TermoDeUso {
            var termoDeUso = TermoDeUso()

            termoDeUso.id = dto.id
            termoDeUso.tipo = dto.tipo
            termoDeUso.texto = dto.texto

            return termoDeUso
        }
    }

    fun toDTO() = TermoDeUsoDto(
        id = this.id,
        tipo = this.tipo,
        texto = this.texto
    )
}

