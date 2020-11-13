package osahner.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Fetch
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "recorrencia")
open class Recorrencia {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "medico_id", referencedColumnName = "id", insertable = true, updatable = true)
  var medico : Medico? = null


  @Column(name = "descricao", nullable = false)
    var descricao: String? = null

    @Column(name = "justa_recorrencia_id", nullable = true)
    var justaRecorrenciaId: String? = null

    @Column(name = "version", nullable = false)
    var version: Long? = null


  @get:JsonIgnore
  @OneToMany(mappedBy = "recorrencia",cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    var cobrancasProgramadas: MutableList<CobrancaProgramada>? = mutableListOf()

   override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "medicoId = $medico " +
                    "descricao = $descricao " +
                    "justaRecorrenciaId = $justaRecorrenciaId " +
                    "version = $version " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Recorrencia

        if (id != other.id) return false
        if (medico != other.medico) return false
        if (descricao != other.descricao) return false
        if (justaRecorrenciaId != other.justaRecorrenciaId) return false
        if (version != other.version) return false

        return true
    }

}

