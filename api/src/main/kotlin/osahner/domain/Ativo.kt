package osahner.domain

import osahner.dto.AtivoDto
import javax.persistence.*

@Entity
@Table(name = "ativo")
open class Ativo {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "valor_residual", nullable = false)
    var valorResidual: Double? = null

    /*@Column(name = "valor_cobravel", nullable = false)
    var valorCobravel: Double? = null*/

    @Column(name = "custo", nullable = false)
    var custo: Double? = null

    @Column(name = "data_entrada", nullable = false)
    var dataEntrada: java.sql.Timestamp? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "cobravel", nullable = false)
    var cobravel: java.lang.Boolean? = null

    @Column(name = "depreciacao", nullable = false)
    var depreciacao: Double? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_equipamento_id", referencedColumnName = "id")
    var tipoEquipamento: TipoEquipamento? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "valorResidual = $valorResidual " +
                    "custo = $custo " +
                    "dataEntrada = $dataEntrada " +
                    "nome = $nome " +
                    "cobravel = $cobravel " +
                    "depreciacao = $depreciacao " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Ativo

        if (id != other.id) return false
        if (version != other.version) return false
        if (valorResidual != other.valorResidual) return false
        if (custo != other.custo) return false
        if (dataEntrada != other.dataEntrada) return false
        if (nome != other.nome) return false
        if (cobravel != other.cobravel) return false
        if (depreciacao != other.depreciacao) return false

        return true
    }

  fun toDTO() = AtivoDto(
    id = this.id,
    version = this.version,
    valorResidual = this.valorResidual,
    //valorCobravel = this.valorCobravel,
    custo = this.custo,
    dataEntrada = this.dataEntrada,
    nome = this.nome,
    cobravel = this.cobravel,
    depreciacao = this.depreciacao,
    tipoEquipamento = this.tipoEquipamento?.toDTO()
  )

  companion object {
    fun fromDTO(dto: AtivoDto): Ativo {
      var ativo : Ativo = Ativo()

      ativo.id = dto.id
      ativo.version = dto.version
      ativo.valorResidual = dto.valorResidual
      ativo.custo = dto.custo
      ativo.dataEntrada = dto.dataEntrada
      ativo.nome = dto.nome
      ativo.cobravel = dto.cobravel
      ativo.depreciacao = dto.depreciacao

      return ativo
    }
  }

}

