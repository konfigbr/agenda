package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.AuditLog

interface AuditLogReepository: JpaRepository<AuditLog, Long> {
}