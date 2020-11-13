package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "credito_utilizado")
@JsonIgnoreProperties("hibernateLazyInitializer")
open class CreditoUtilizado {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "uuid_transacao", nullable = true)
    var uuidTransacao: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1



  @Column(name = "valor", nullable = false)
    var valor: Double? = null


    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatura_id", referencedColumnName = "id")
    var fatura: Fatura? = null
    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credito_id", referencedColumnName = "id")
    var credito: Credito? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "valor = $valor " +
                    ")"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CreditoUtilizado

    if (id != other.id) return false
    if (uuidTransacao != other.uuidTransacao) return false
    if (valor != other.valor) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id?.hashCode() ?: 0
    result = 31 * result + (uuidTransacao?.hashCode() ?: 0)
    result = 31 * result + (valor?.hashCode() ?: 0)
    return result
  }

}

