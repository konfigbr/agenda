package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.*
import kotlin.jvm.Transient

@JsonIgnoreProperties(ignoreUnknown = true)
@MappedSuperclass
abstract class PessoaAbstractJpa<T : Serializable> {
    companion object {
        private val serialVersionUID = -5554308939380869754L
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     var id: T? = null

    @Column(name = "telefone", nullable = true)
    var telefone: String? = null

    @Column(name = "rg", nullable = true)
    var rg: String? = null

    @Column(name = "cep", nullable = true)
    var cep: String? = null

    @Column(name = "endereco", nullable = true)
    var endereco: String? = null

    @Column(name = "imagerg", nullable = true)
    var imagerg: String? = null

    @Column(name = "cpf", nullable = true)
    var cpf: String? = null

    @Column(name = "imagecpf", nullable = true)
    var imagecpf: String? = null


    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as PessoaAbstractJpa<*>

      if (id != other.id) return false

      return true
    }

    override fun hashCode(): Int {
      return id?.hashCode() ?: 0
    }
}
