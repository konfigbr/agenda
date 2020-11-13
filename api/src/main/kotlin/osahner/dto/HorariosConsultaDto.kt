package osahner.dto

import osahner.enums.StatusFatura
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column

data class HorariosConsultaDto (
    var idConsulta: Long?,
    var idAgendamento: Long?,
    var dataInicio: LocalDateTime? = null,
    var dataFim: LocalDateTime? = null
)