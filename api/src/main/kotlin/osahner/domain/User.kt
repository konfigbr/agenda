package osahner.domain

import org.hibernate.annotations.Proxy
import osahner.dto.AtivoDto
import osahner.dto.UserAdmAssistenteDto
import osahner.dto.UserDto
import javax.persistence.*

@Entity
@Table(name = "user")
@Proxy(lazy=false)
open class User {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "version", nullable = false)
    var version: Long? = null

    @Column(name = "password_expired", nullable = false)
    var passwordExpired: Boolean? = null

    @Column(name = "password_recovered", nullable = false)
    var passwordRecovered: Boolean? = null

    @Column(name = "username", nullable = false)
    var username: String? = null

    @Column(name = "account_locked", nullable = false)
    var accountLocked: Boolean? = null

    @Column(name = "password", nullable = false)
    var password: String? = null

    @Column(name = "nome", nullable = false)
    var nome: String? = null

    @Column(name = "account_expired", nullable = false)
    var accountExpired: Boolean? = null

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean? = null

    @Column(name = "ativo", nullable = false, columnDefinition = "varchar(1) default 'S'"  )
    var ativo: String? = "S"

    @ManyToMany(fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.ALL))
    @JoinTable(
      name = "user_role",
      joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
      inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: MutableList<Role>? = null

    override fun toString(): String {
      return "User(" +
        "id=$id, " +
        "version=$version, " +
        "passwordExpired=$passwordExpired, " +
        "passwordRecovered=$passwordRecovered, " +
        "username=$username, " +
        "accountLocked=$accountLocked, " +
        "password=$password, " +
        "nome=$nome, " +
        "accountExpired=$accountExpired, " +
        "enabled=$enabled, " +
        "roles=$roles" +
        ")"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as User

      if (id != other.id) return false

      return true
    }

    override fun hashCode(): Int {
      return id?.hashCode() ?: 0
    }

  fun toDTO() = UserDto(
    id = this.id,
    version = this.version,
    passwordExpired = this.passwordExpired,
    passwordRecovered = this.passwordRecovered,
    username = this.username,
    accountLocked = this.accountLocked,
    password = this.password,
    nome = this.nome,
    accountExpired = this.accountExpired,
    enabled = this.enabled,
    ativo = this.ativo
  )

  companion object {
    fun fromDTO(dto: UserDto): User {
      var user : User = User()

      user.id = dto.id
      user.version = dto.version
      user.passwordExpired = dto.passwordExpired
      user.passwordRecovered = dto.passwordRecovered
      user.username = dto.username
      user.accountLocked = dto.accountLocked
      user.password = dto.password
      user.nome = dto.nome
      user.accountExpired = dto.accountExpired
      user.enabled = dto.enabled
      user.ativo = dto.ativo

      return user
    }
  }

  fun toAdmAssDTO() = UserAdmAssistenteDto(
    id = this.id,
    username = this.username,
    nome = this.nome,
    accessType = null

  )
}

