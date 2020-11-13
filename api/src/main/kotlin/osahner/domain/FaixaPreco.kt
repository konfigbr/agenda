package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import osahner.dto.FaixaPrecoDto
import osahner.dto.TipoEquipamentoDto
import javax.persistence.*

@Entity
@Table(name = "faixa_preco")
open class FaixaPreco {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "valor", nullable = false)
    var valor: Double? = null

    @Column(name = "total_horas", nullable = false)
    var totalHoras: Int? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabela_preco_id", referencedColumnName = "id", nullable = true, insertable = true, updatable = true)
    var tabelaPreco: TabelaPreco? = null

    override fun toString(): String {
        return "FaixaPreco(id=$id, version=$version, valor=$valor, totalHoras=$totalHoras)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaixaPreco

        if (id != other.id) return false
        if (version != other.version) return false
        if (valor != other.valor) return false
        if (totalHoras != other.totalHoras) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (version?.hashCode() ?: 0)
        result = 31 * result + (valor?.hashCode() ?: 0)
        result = 31 * result + (totalHoras ?: 0)
        return result
    }

    fun toDTO() = FaixaPrecoDto(
        id = this.id,
        totalHoras = this.totalHoras,
        valor = this.valor,
        tabelaPreco = this.tabelaPreco
    )

    companion object {
        fun fromDTO(dto: FaixaPrecoDto): FaixaPreco {
          var faixaPreco = FaixaPreco()

          faixaPreco.id = dto.id
          faixaPreco.totalHoras = dto.totalHoras
          faixaPreco.valor = dto.valor
          faixaPreco.tabelaPreco = dto.tabelaPreco

          return faixaPreco
        }
    }
}

