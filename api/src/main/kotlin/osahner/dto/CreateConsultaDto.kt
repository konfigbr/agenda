package osahner.dto

import osahner.domain.Consulta
import java.time.LocalDateTime
import java.time.ZoneId
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

open class CreateConsultaDto {
    @NotNull(message = "A data de início precisa ser informada")
    var dataInicio: LocalDateTime? = null

    @NotNull(message = "A data de fim precisa ser informada")
    var dataFim: LocalDateTime? = null

    @NotBlank(message = "O motivo da consulta precisa ser informado")
    var motivo: String? = null

    @NotNull(message = "O agendamento da sala precisa ser informado")
    var agendamentoId: Long? = null

    var pacienteId: Long? = null

    fun toConsulta() = this.let {
        Consulta().apply {
            dataInicio = it.dataInicio
            dataFim = it.dataFim
            motivo = it.motivo
        }
    }
}