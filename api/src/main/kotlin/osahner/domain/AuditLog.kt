package osahner.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import osahner.dto.AuditLogDto
import osahner.dto.SalaDto
import osahner.dto.TipoEquipamentoDto
import osahner.enums.CreditoTipo
import osahner.enums.LogEnum
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "log_auditoria")
open class AuditLog {
    @Id
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: User? = null

    @Column(name = "tipo_log", nullable = true)
    @Enumerated(EnumType.STRING)
    var tipoLog: LogEnum? = null

    @Column(name = "date_created", nullable = false)
    var dateCreated: LocalDateTime? = null

    @Column(name = "descricao", nullable = true)
    var descricao: String? = null

    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuditLog

        if (id != other.id) return false
        if (tipoLog != other.tipoLog) return false
        if (dateCreated != other.dateCreated) return false
        if (descricao != other.descricao) return false

        return true
    }

    fun toDTO() = AuditLogDto(
        id = this.id,
        user = this.user,
        tipoLog = this.tipoLog,
        dateCreated = this.dateCreated,
        descricao = this.descricao
    )

   companion object {
    fun fromDTO(dto: AuditLogDto): AuditLog {
      var auditLog = AuditLog()

       auditLog.id = dto.id
       auditLog.user = dto.user
       auditLog.tipoLog = dto.tipoLog
       auditLog.dateCreated = dto.dateCreated
       auditLog.descricao = dto.descricao

       return auditLog
    }
  }
}

