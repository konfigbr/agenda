package osahner.dto

import osahner.domain.AgendaSalaTipoEquipamento
import osahner.domain.Agendamento
import java.time.LocalDate
import java.time.LocalTime


open class  ResumoMensalDto {


  var mesAno: LocalDate? = null
  var custo: Double? = 0.0
  var horaInicio: LocalTime? = null
  var agendamentos: MutableList<Agendamento>? = mutableListOf()
  var equipamentos: MutableList<AgendaSalaTipoEquipamento> = mutableListOf()

}
