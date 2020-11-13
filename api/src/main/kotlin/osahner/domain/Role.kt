package osahner.domain

import javax.persistence.*

@Entity
@Table(name = "role")
class Role(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,

  @Column(name = "role_name")
  var roleName: String? = null,

  @Column(name = "description")
  var description: String? = null
)
