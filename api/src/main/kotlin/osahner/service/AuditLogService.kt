package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.AuditLog
import osahner.domain.User
import osahner.dto.AuditLogDto
import osahner.enums.LogEnum
import osahner.repository.AuditLogReepository
import java.time.LocalDateTime

@Component
class AuditLogService(
    private val auditLogReepository: AuditLogReepository
) {
    fun list(): Collection<AuditLog> = auditLogReepository.findAll().sortedByDescending { it.dateCreated }

    fun save(dto: AuditLogDto): AuditLog = auditLogReepository.saveAndFlush(AuditLog.fromDTO(dto))

    fun criarLog(user: User, tipoLog: LogEnum, descricao: String? = null) {

        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone)

        var dto = AuditLogDto(
            id = null,
            user = user,
            tipoLog = tipoLog,
            dateCreated = now,
            descricao = descricao
        )

        auditLogReepository.saveAndFlush(AuditLog.fromDTO(dto))
    }
}