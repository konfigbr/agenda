package osahner.domain

import osahner.dto.SalaDto
import osahner.dto.TipoEquipamentoDto
import javax.persistence.*

@Entity
@Table(name = "tipo_equipamento")
open class TipoEquipamento {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "valor_cobravel", nullable = false)
    var valorCobravel: Double? = null

    @OneToMany(mappedBy = "tipoEquipamento")
    var refAgendaSalaTipoEquipamentoEntities: MutableList<AgendaSalaTipoEquipamento>? = null
    @OneToMany(mappedBy = "tipoEquipamento")
    var refAtivoEntities: MutableList<Ativo>? = null
    @OneToMany(mappedBy = "tipoEquipamento")
    var refSolicitacaoEntities: MutableList<SolicitacaoEquipamento>? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "nome = $nome " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TipoEquipamento

        if (id != other.id) return false
        if (version != other.version) return false
        if (nome != other.nome) return false

        return true
    }

  fun toDTO() = TipoEquipamentoDto(
    id = this.id,
    version = this.version,
    valorCobravel = this.valorCobravel,
    nome = this.nome
  )

  companion object {
    fun fromDTO(dto: TipoEquipamentoDto): TipoEquipamento {
      var tipoEquipamento : TipoEquipamento = TipoEquipamento()

      tipoEquipamento.id = dto.id
      tipoEquipamento.version = dto.version
      tipoEquipamento.valorCobravel = dto.valorCobravel
      tipoEquipamento.nome = dto.nome

      return tipoEquipamento
    }
  }
}

