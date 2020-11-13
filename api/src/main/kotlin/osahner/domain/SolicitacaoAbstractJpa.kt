package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.util.ProxyUtils
import osahner.enums.SolicitacaoStatus
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
abstract class SolicitacaoAbstractJpa<T : Serializable> {
    companion object {
        private val serialVersionUID = -5554308939380869754L
    }

  @Id
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null

  @Column(name = "version", nullable = false)
  var version: Long? = 1

  @Column(name = "fim", nullable = false)
  var fim: LocalDateTime? = null

  @Column(name = "date_created", nullable = false)
  var dateCreated: LocalDateTime? = null

  @Column(name = "custo", nullable = false)
  var custo: Double? = 0.0

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  var status: SolicitacaoStatus? = null

  @Column(name = "inicio", nullable = false)
  var inicio: LocalDateTime? = null

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "carrinho_id", referencedColumnName = "id", insertable = true, updatable = true)
  @get:JsonIgnore
  var refCarrinhoCompraEntity: CarrinhoCompra? = null

  @get:JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id", referencedColumnName = "id", insertable = true, updatable = true)
  var refUserEntity: Medico? = null

  @get:JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sala_id", referencedColumnName = "id", insertable = true, updatable = true)
  var refSalaEntity: Sala? = null


}
