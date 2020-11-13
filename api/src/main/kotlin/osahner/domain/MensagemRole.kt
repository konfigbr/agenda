package osahner.domain

import javax.persistence.*

@Entity
@Table(name = "mensagem_role")
open class MensagemRole {
   @Id
   @Column(name = "id", nullable = false, insertable = false, updatable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   var id: Long? = null


  @Column(name = "mensagem_perfis_destinatarios_id", nullable = false, insertable = false, updatable = false)
  var mensagemPerfisDestinatariosId: Long? = null


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mensagem_perfis_destinatarios_id", referencedColumnName = "id")
  var refMensagemEntity: Mensagem? = null
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", referencedColumnName = "id")
  var refRoleEntity: Role? = null

  override fun toString(): String =
    "Entity of type: ${javaClass.name} ( " +
      "mensagemPerfisDestinatariosId = $mensagemPerfisDestinatariosId " +
      "roleId = $refRoleEntity " +
      ")"

  // constant value returned to avoid entity inequality to itself before and after it's update/merge
  override fun hashCode(): Int = 42

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as MensagemRole

    if (mensagemPerfisDestinatariosId != other.mensagemPerfisDestinatariosId) return false
    if (refRoleEntity != other.refRoleEntity) return false

    return true
  }

}

