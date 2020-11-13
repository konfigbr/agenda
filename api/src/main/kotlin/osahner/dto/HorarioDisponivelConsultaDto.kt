package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class HorarioDisponivelConsultaDto(
    var dataInicio: LocalDateTime?,
    var dataFim: LocalDateTime?
)

