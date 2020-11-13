package osahner.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import osahner.domain.*
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import java.sql.Timestamp

import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgendamentoGetDto(



  var agendamento: Agendamento?,
  var sala: Sala?,
  var assistente: Assistente?,
  var profissional: Medico?,
  var inicio: LocalDateTime?,
  var fim: LocalDateTime?,
  var horarioSalaOcupada: LocalDateTime?,
  var horarioSalaLiberada: LocalDateTime?,
  var custoHora: Double?,
  var tiposEquipamentos: Set<AgendaSalaTipoEquipamento>?,
  var tipoReserva: TipoReserva?,
  var nomeProfissional: String?,
  var nomeAssistente: String?,
  var checkinAssistente: LocalDateTime?,
  var checkinProfessional: LocalDateTime?,
  var checkoutAssitente: LocalDateTime?,
  var checkoutProfessional: LocalDateTime?,
  var status: SolicitacaoStatus?

  )
