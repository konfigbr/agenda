package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.dto.EspecialidadeDto
import osahner.dto.LocalDto
import javax.persistence.*

@Entity
@Table(name = "local")
@JsonIgnoreProperties(ignoreUnknown = true)
open class Local {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = true)
    var version: Long? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null
     @get:JsonIgnore
    @OneToMany(mappedBy = "refLocalEntity")
    var refSalaEntities: MutableList<Sala>? = null

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "id = $id " +
                    "version = $version " +
                    "nome = $nome " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Local

        if (id != other.id) return false
        if (version != other.version) return false
        if (nome != other.nome) return false

        return true
    }

  fun toDTO() = LocalDto(
    id = this.id,
    version = this.version,
    nome = this.nome
  )

  companion object {
    fun fromDTO(dto: LocalDto): Local {
      var local: Local = Local()

      local.id = dto.id
      local.version = dto.version
      local.nome = dto.nome

      return local
    }
  }
}

