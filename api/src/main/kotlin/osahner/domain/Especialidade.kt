package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.EspecialidadeDto
import osahner.dto.SalaDto
import osahner.enums.StatusFatura
import osahner.enums.TipoAceiteEspecialidade
import javax.persistence.*

@Entity
@Table(name = "especialidade")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Especialidade {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "exibir_cadastro_profissional", nullable = false)
    var exibirCadastroProfissional: Boolean? = true

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_aceite", nullable = false)
    var tipoAceite: TipoAceiteEspecialidade? = null

    @ManyToOne
    @JoinColumn(name = "tabela_preco_id", referencedColumnName = "id", nullable = false)
    var tabelaPreco: TabelaPreco? = null

    @get:JsonIgnore
    @OneToMany(mappedBy = "refEspecialidadeEntity", fetch = FetchType.LAZY)
    var refSalaEntities: MutableList<Sala>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Especialidade

        if (id != other.id) return false
        if (version != other.version) return false
        if (nome != other.nome) return false
        if (tabelaPreco != other.tabelaPreco) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (version?.hashCode() ?: 0)
        result = 31 * result + (nome?.hashCode() ?: 0)
        result = 31 * result + (tabelaPreco?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Especialidade(id=$id, version=$version, nome=$nome, tabelaPreco=$tabelaPreco)"
    }

    companion object {
        fun fromDTO(dto: EspecialidadeDto): Especialidade {
          var especialidade = Especialidade()

          especialidade.id = dto.id
          especialidade.version = dto.version
          especialidade.nome = dto.nome
          especialidade.tabelaPreco = dto.tabelaPreco
          especialidade.exibirCadastroProfissional = dto.exibirCadastroProfissional
          especialidade.tipoAceite = dto.tipoAceite

          return especialidade
        }
    }

    fun toDTO() = EspecialidadeDto(
        id = this.id,
        version = this.version,
        nome = this.nome,
        tabelaPreco = this.tabelaPreco,
        exibirCadastroProfissional = this.exibirCadastroProfissional,
        tipoAceite = this.tipoAceite
    )
}

