package osahner.dto

import osahner.domain.*
import osahner.enums.AgendamentoStatus
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import java.time.LocalDate
import java.time.LocalDateTime

data class AgendamentoCustomDto(
  var id: Long?=null,
  var version: Long?=null,
  var dia: LocalDate?=null,
  var dateCreated: LocalDateTime?=null,
  var custo: Double=0.0,
  var agendamentoStatusAtual: AgendamentoStatus?=null,
  var lastUpdated: LocalDateTime?=null,
  var status: SolicitacaoStatus?=null,
  var periodo: PeriodoAgendamento?=null,
  var user: MedicoCustomDto?=null,
  var sala: Sala?=null,
  var assistente: AssistenteCustomDto?=null,
  var inicio: LocalDateTime ?=null,
  var fim: LocalDateTime ?=null,
  var horarioSalaOcupada: LocalDateTime ?=null,
  var horarioSalaLiberada: LocalDateTime ?=null,
  var custoHora : Double ?=null,
  var tipoReserva : TipoReserva?=null,
  var tiposEquipamentos: MutableList<AgendaSalaTipoEquipamentoDto> ?= mutableListOf(),
  var assistantCheckinHour: LocalDateTime? = null,
  var assistantCheckoutHour: LocalDateTime? = null,
  var professionalCheckinHour: LocalDateTime? = null,
  var professionalCheckoutHour: LocalDateTime? = null,
  var solicitacaoReserva : Long ?=null
  ){
  companion object {
    fun fromDto(agDto: AgendamentoDto): AgendamentoCustomDto {
      var dto = AgendamentoCustomDto()

      val assitenteCheckIn = agDto.historicosAgendamentos?.filter { it?.refUserEntity?.id == agDto.assistente?.idUser && it?.status == AgendamentoStatus.OK }
      val assitenteCheckout = agDto.historicosAgendamentos?.filter { it?.refUserEntity?.id == agDto.assistente?.idUser && it?.status == AgendamentoStatus.LIBERADO }
      val profissionalCheckin = agDto.historicosAgendamentos?.filter { it?.refUserEntity?.id == agDto.user?.user?.id  && it?.status == AgendamentoStatus.OK }
      val profissionalCheckout = agDto.historicosAgendamentos?.filter { it?.refUserEntity?.id == agDto.user?.user?.id  && it?.status == AgendamentoStatus.LIBERADO }

      dto.id=agDto.id
      dto.version=agDto.version
      dto.dia=agDto.dia
      dto.dateCreated=agDto.dateCreated
      dto.custo=agDto.custo
      dto.agendamentoStatusAtual=agDto.agendamentoStatusAtual
      dto.lastUpdated=agDto.lastUpdated
      dto.status=agDto.status
      dto.periodo=agDto.periodo
      dto.user=agDto.user?.toCustomDTO()
      dto.sala=agDto.sala
      dto.assistente=agDto.assistente
      dto.inicio=agDto.inicio
      dto.fim=agDto.fim
      dto.horarioSalaOcupada=agDto.horarioSalaOcupada
      dto.horarioSalaLiberada=agDto.horarioSalaLiberada
      dto.custoHora=agDto.custoHora
      dto.tipoReserva=agDto.tipoReserva
      dto.tiposEquipamentos= agDto.tiposEquipamentos?.map{ it.toCustomDto() } as MutableList<AgendaSalaTipoEquipamentoDto>?
      dto.assistantCheckinHour = if (assitenteCheckIn?.size != 0) assitenteCheckIn?.first()?.dateCreated else null
      dto.assistantCheckoutHour = if (assitenteCheckout?.size != 0) assitenteCheckout?.first()?.dateCreated else null
      dto.professionalCheckinHour = if (profissionalCheckin?.size != 0) profissionalCheckin?.first()?.dateCreated else null
      dto.professionalCheckoutHour = if (profissionalCheckout?.size != 0) profissionalCheckout?.first()?.dateCreated else null

      return dto
    }
  }
}
