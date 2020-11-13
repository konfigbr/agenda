package osahner.repository

import osahner.domain.Consulta
import osahner.enums.StatusConsulta
import java.time.LocalDateTime

interface ConsultaRepositoryCustom {
    fun findByCriteria(
            medicoId: Long?,
            dataInicio: LocalDateTime?,
            dataFim: LocalDateTime?,
            salaId: Long?,
            status: StatusConsulta?,
            pacienteId: Long?
    ): MutableList<Consulta>
}