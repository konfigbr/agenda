package osahner.domain
import javax.persistence.*

@Entity
@Table(name = "empresa_medico")
class EmpresaMedico {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    var medico: Medico? = null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    var empresa: Empresa? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "medico_id = $medico " +
                    "empresa_id = $empresa " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EmpresaMedico

        if (medico != other.medico) return false
        if (empresa != other.empresa) return false

        return true
    }
}