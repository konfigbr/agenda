package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "cobranca_programada")
@JsonIgnoreProperties(ignoreUnknown = true)
open class CobrancaProgramada {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "recorrencia_id", referencedColumnName = "id")
    var recorrencia: Recorrencia? = null

    @get:JsonIgnore
    @ManyToOne
    @JoinColumn(name = "fatura_id", referencedColumnName = "id")
    var fatura: Fatura? = null


    @Column(name = "sequencia_cobranca", nullable = false)
    var sequenciaCobranca: Int? = null

    @Column(name = "data_cobranca", nullable = false)
    var dataCobranca: LocalDate? = null

    @Column(name = "valor", nullable = false)
    var valor: Double? = null

    @Column(name = "justa_cobranca_id", nullable = true)
    var justaCobrancaId: String? = null

    @Column(name = "version", nullable = false)
    var version: Long? = 1

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "recorrenciaId = $recorrencia " +
                    "sequenciaCobranca = $sequenciaCobranca " +
                    "dataCobranca = $dataCobranca " +
                    "valor = $valor " +
                    "justaCobrancaId = $justaCobrancaId " +
                    "version = $version " +
                    ")"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CobrancaProgramada

    if (id != other.id) return false

    return true
  }

  override fun hashCode(): Int {
    return id?.hashCode() ?: 0
  }

}

