package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Proxy
import osahner.enums.CreditoTipo
import osahner.enums.PeriodoAgendamento
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "credito")
@JsonIgnoreProperties(ignoreUnknown = true)
@Proxy(lazy=false)
open class Credito {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long? = null


  @Column(name = "version", nullable = false)
    var version: Long? = 1

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Column(name = "last_updated", nullable = true)
    var lastUpdated: LocalDateTime? = null

    @Column(name = "valor_total", nullable = false)
    var valorTotal: Double? = null

    @Column(name = "valor_disponivel", nullable = false)
    var valorDisponivel: Double? = null

    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var medico: Medico? = null
    @get:JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", referencedColumnName = "id")
    var agendamento: Agendamento? = null
    @get:JsonIgnore
    @OneToMany(mappedBy = "credito", cascade = [CascadeType.ALL],fetch = FetchType.EAGER)
    var creditoAgendaSalaTipoEquipamento: MutableList<CreditoAgendaSalaTipoEquipamento> = mutableListOf()
    @get:JsonIgnore
    @OneToMany(mappedBy = "credito")
    var creditoUtilizado: MutableList<CreditoUtilizado>? = null

    @Column(name = "tipo", nullable = true)
    @Enumerated(EnumType.STRING)
    //@Transient
    var tipo: CreditoTipo? = null


   override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "dateCreated = $dateCreated " +
                    "lastUpdated = $lastUpdated " +
                    "valorTotal = $valorTotal " +
                    "valorDisponivel = $valorDisponivel " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Credito

        if (id != other.id) return false
        if (version != other.version) return false
        if (dateCreated != other.dateCreated) return false
        if (lastUpdated != other.lastUpdated) return false
        if (valorTotal != other.valorTotal) return false
        if (valorDisponivel != other.valorDisponivel) return false

        return true
    }

}

