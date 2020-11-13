package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.AgendamentoStatus
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import java.time.LocalDate
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoListarMedicoDto(

  var id: Long?=null,
  var version: Long?=null,
  var dia: LocalDate?=null,
  var dateCreated: LocalDateTime?=null,
  var custo: Double?=null,
  var agendamentoStatusAtual: AgendamentoStatus?=null,
  var lastUpdated: LocalDateTime?=null,
  var status: SolicitacaoStatus?=null,
  var periodo: PeriodoAgendamento?=null,
  var user: MedicoCustomDto?=null,
  var fatura: Fatura?=null,
  var sala: Sala?=null,
  var assistente: AssistenteCustomDto?=null,
  var inicio: LocalDateTime ?=null,
  var fim: LocalDateTime ?=null,
  var horarioSalaOcupada: LocalDateTime ?=null,
  var horarioSalaLiberada: LocalDateTime ?=null,
  var custoHora : Double ?=null,
  var tipoReserva : TipoReserva?=null,
  var tiposEquipamentos: MutableList<AgendaSalaTipoEquipamentoDto>? = mutableListOf()

)
