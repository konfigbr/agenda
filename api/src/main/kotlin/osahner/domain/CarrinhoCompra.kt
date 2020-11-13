package osahner.domain


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference

import osahner.dto.CarrinhoCompraDto

import osahner.dto.CarrinhoDto
import osahner.dto.UserDto
import osahner.enums.SolicitacaoStatus
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "carrinho_compra")
@JsonIgnoreProperties(ignoreUnknown = true)
open class CarrinhoCompra {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1

    @Column(name = "valor", nullable = false)
    var valor: Double? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Column(name = "custo", nullable = false)
    var custo: Double? = null


    @Column(name = "last_updated", nullable = true)
    var lastUpdated: LocalDateTime? = null

    @Column(name = "desconto", nullable = false)
    var desconto: Double? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: SolicitacaoStatus? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var medico: Medico? = null

    @JsonIgnoreProperties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    var createUser: User? = null

    @OneToMany(mappedBy = "refCarrinhoCompraEntity")
    @JsonIgnore
    var refFaturaEntities: MutableList<Fatura>? = mutableListOf()

    @OneToMany(mappedBy = "refCarrinhoCompraEntity", fetch = FetchType.EAGER)
    @JsonIgnoreProperties
    var solicitacoesReserva: MutableSet<SolicitacaoReserva>? =  mutableSetOf()

    @OneToMany(mappedBy = "refCarrinhoCompraEntity", fetch = FetchType.EAGER)
    @JsonIgnoreProperties
    var solicitacoesEquipamento: MutableSet<SolicitacaoEquipamento>? = mutableSetOf()


  override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "valor = $valor " +
                    "dateCreated = $dateCreated " +
                    "custo = $custo " +
                    "lastUpdated = $lastUpdated " +
                    "desconto = $desconto " +
                    "status = $status " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CarrinhoCompra

        if (id != other.id) return false
        if (valor != other.valor) return false
        if (dateCreated != other.dateCreated) return false
        if (custo != other.custo) return false
        if (lastUpdated != other.lastUpdated) return false
        if (desconto != other.desconto) return false
        if (status != other.status) return false

        return true
    }

    fun toDTO() = CarrinhoCompraDto(
      id = this.id,
      valor = this.valor,
      dateCreated = this.dateCreated,
      custo = this.custo,
      lastUpdated = this.lastUpdated,
      desconto = this.desconto,
      status = this.status,
      createdBy = this.medico?.toDTO(),
      usuario = this.createUser?.toDTO()
    )

    companion object {
      fun fromDTO(dto: CarrinhoCompraDto): CarrinhoCompra {
        var carrinho : CarrinhoCompra = CarrinhoCompra()

        carrinho.id = dto.id
        carrinho.valor = dto.valor
        carrinho.dateCreated = dto.dateCreated
        carrinho.custo = dto.custo
        carrinho.lastUpdated = dto.lastUpdated
        carrinho.desconto = dto.desconto
        carrinho.status = dto.status

        return carrinho
      }

    }

}

