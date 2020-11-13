package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.AgendamentoStatus
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import java.sql.Timestamp
import java.time.LocalDate

import java.time.LocalDateTime
import java.util.*
@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoDto(

  var id: Long?=null,

  var version: Long?=null,

  var dia: LocalDate?=null,

  var dateCreated: LocalDateTime?=null,

  var custo: Double=0.0,

  var agendamentoStatusAtual: AgendamentoStatus?=null,

  var lastUpdated: LocalDateTime?=null,

  var status: SolicitacaoStatus?=null,

  var periodo: PeriodoAgendamento?=null,

  //var solicitacao: Solicitacao?=null,

  var user: Medico?=null,

  var sala: Sala?=null,

  var assistente: AssistenteCustomDto?=null,

  var inicio: LocalDateTime ?=null,

  var fim: LocalDateTime ?=null,

  var horarioSalaOcupada: LocalDateTime ?=null,

  var horarioSalaLiberada: LocalDateTime ?=null,

  var custoHora : Double ?=null,

  var tipoReserva : TipoReserva?=null,

  var tiposEquipamentos: MutableList<AgendaSalaTipoEquipamento> ?= mutableListOf(),

  var historicosAgendamentos: MutableList<HistoricoAgendamentoStatus?> ?= mutableListOf(),

  var alterou : Boolean ?=null

)
