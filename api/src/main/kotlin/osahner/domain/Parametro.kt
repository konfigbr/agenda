package osahner.domain

import osahner.dto.ParametroDto
import javax.persistence.*

@Entity
@Table(name = "parametro")
open class Parametro {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "tipo", nullable = false)
    var tipo: String? = null

    @Column(name = "valor", nullable = false)
    var valor: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Parametro

        if (id != other.id) return false
        if (tipo != other.tipo) return false
        if (valor != other.valor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (tipo?.hashCode() ?: 0)
        result = 31 * result + (valor?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Parametro(id=$id, tipo=$tipo, valor=$valor)"
    }

    companion object {
        fun fromDTO(dto: ParametroDto): Parametro {
            var parametro = Parametro()

            parametro.id = dto.id
            parametro.tipo = dto.tipo
            parametro.valor = dto.valor

            return parametro
        }
    }

    fun toDTO() = ParametroDto(
        id = this.id,
        tipo = this.tipo,
        valor = this.valor
    )
}

