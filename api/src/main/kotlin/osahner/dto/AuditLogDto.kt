package osahner.dto

import osahner.domain.User
import osahner.enums.LogEnum
import java.time.LocalDateTime

data class AuditLogDto(
        var id: Long?,

        var user: User?,

        var tipoLog: LogEnum?,

        var dateCreated: LocalDateTime?,

        var descricao: String?
)
