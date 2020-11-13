package osahner.domain

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import osahner.dto.TabelaPrecoDto
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "tabela_preco")
open class TabelaPreco {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @OneToMany(mappedBy = "tabelaPreco",cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    var faixaPrecos: MutableList<FaixaPreco?> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TabelaPreco

        if (id != other.id) return false
        if (nome != other.nome) return false
        if (faixaPrecos != other.faixaPrecos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (nome?.hashCode() ?: 0)
        result = 31 * result + (faixaPrecos?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TabelaPreco(id=$id, nome=$nome, faixaPrecos=$faixaPrecos)"
    }

    companion object {
        fun fromDTO(dto: TabelaPrecoDto): TabelaPreco {
            var tabelaPreco = TabelaPreco()

            tabelaPreco.id = dto.id
            tabelaPreco.nome = dto.nome
            tabelaPreco.faixaPrecos = dto.faixaPrecos

            return tabelaPreco
        }
    }

    fun toDTO() = TabelaPrecoDto(
        id = this.id,
        nome = this.nome,
        faixaPrecos = this.faixaPrecos
    )
}

