package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "manutencao")
open class Manutencao {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: java.sql.Timestamp? = null

    @Column(name = "last_updated", nullable = false)
    var lastUpdated: java.sql.Timestamp? = null

    @OneToMany(mappedBy = "manutencao")
    @get:JsonIgnore
    var refAgendaSalaEntities: MutableList<AgendaSala>? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "dateCreated = $dateCreated " +
                    "lastUpdated = $lastUpdated " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Manutencao

        if (id != other.id) return false
        if (version != other.version) return false
        if (dateCreated != other.dateCreated) return false
        if (lastUpdated != other.lastUpdated) return false

        return true
    }

}

