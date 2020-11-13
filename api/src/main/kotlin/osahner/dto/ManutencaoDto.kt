package osahner.dto

import java.time.LocalDateTime

data class ManutencaoDto (
        var sala: Long?,

        var periodoAgendamento : Int?,

        var inicio: LocalDateTime?,

        var fim: LocalDateTime?
)