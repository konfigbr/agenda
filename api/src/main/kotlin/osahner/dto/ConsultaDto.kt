package osahner.dto

import osahner.enums.StatusConsulta
import java.time.LocalDateTime

data class ConsultaDto (
        var id: Long?,
        var version: Long?,
        var paciente: PacienteCustomDto?,
        var medico: MedicoCustomDto?,
        var dataInicio: LocalDateTime?,
        var dataFim: LocalDateTime?,
        var motivo: String?,
        var status: StatusConsulta?,
        var observacao: String?
)