package osahner.dto

import osahner.domain.*
import osahner.enums.TipoReserva
import java.time.LocalDateTime

class AgendamentoAgruparDto (
  agendamento: Agendamento? = null,
  sala: Sala? = null,
  assistente: Assistente? = null,
  medico: Medico? = null,
  inicio: LocalDateTime? = null,
  fim: LocalDateTime? = null,
  horarioSalaOcupada: LocalDateTime? = null,
  horarioSalaLiberada: LocalDateTime? = null,
  custoHora: Double? = null,
  tiposEquipamentos: MutableList<AgendaSalaTipoEquipamento>? = null,
  tipoReserva: TipoReserva? = null
)
