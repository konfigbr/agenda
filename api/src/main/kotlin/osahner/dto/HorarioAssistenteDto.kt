package osahner.dto

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class HorarioAssistenteDto(
    var assistente: String?,
    var dia: String?,
    var inicio: LocalTime?,
    var fim: LocalTime?
)

