package osahner.enums

import osahner.dto.SalaDto
import osahner.dto.TipoEquipamentoDto
import javax.persistence.*

enum class PeriodoAgendamento(val value : Int) {
  UMA_HORA(1), DUAS_HORAS(2), QUATRO_HORAS(4);

  companion object {
    fun fromInt(value: Int) = PeriodoAgendamento.values().first { it.value == value }
  }
}


