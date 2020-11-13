package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.AtivoDto
import osahner.dto.AuditLogDto
import osahner.enums.LogEnum
import osahner.service.AtivoService
import osahner.service.AuditLogService
import osahner.service.UserService
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/auditLog")
class AuditLogController(
        private val auditLogService: AuditLogService,
        private val userService: UserService
) {
    @GetMapping(value = ["", "/"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
    fun list() = auditLogService.list().map { it.toDTO() }

    @PostMapping(value = ["/logout"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ASSISTENTE', 'ROLE_ATENDENTE', 'ROLE_PACIENTE')")
    fun save() {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

        ResponseEntity.status(HttpStatus.CREATED).body(auditLogService.criarLog(currentUser, LogEnum.LOGOUT, null))
    }
}