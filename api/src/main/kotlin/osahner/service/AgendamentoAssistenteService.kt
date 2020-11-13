package osahner.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime


import osahner.domain.AgendaAssistente
import osahner.dto.AgendaAssistenteDto
import osahner.repository.AgendaAssistenteRepository

import osahner.domain.*
import osahner.dto.*
import osahner.enums.AgendamentoStatus
import osahner.enums.TipoReserva
import osahner.repository.*
import java.sql.Timestamp


@Component
class AgendamentoAssistenteService(
        private val agendaAssistenteRepository: AgendaAssistenteRepository,
        private val agendamentoSalaService: AgendamentoSalaService,
        private val agendamentoRepository:AgendamentoRepository,
        private val agendaSalaRepository: AgendaSalaRepository,
        private val salaRepository: SalaRepository,
        private val assistenteRepository: AssistenteRepository,
        private val agendaSalaEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
        private val historicoAgendamentoStatusRepository : HistoricoAgendamentoStatusRepository,
        private val userRepository: UserRepository

) {

  fun listarAgendamentos(dto: AgendamentoAssistenteDto) : MutableList<AgendamentoCustomDto> {

    val inicio = LocalDateTime.parse(dto.inicio)
    val fim = LocalDateTime.parse(dto.fim)
    val result = mutableListOf<AgendamentoCustomDto>()
    val itensAgendamento = agendamentoRepository.findByDiaAtualAssistente(dto.id, inicio, fim)

    itensAgendamento!!.forEach { it ->
      var item = AgendamentoCustomDto.fromDto(buildAgendamentoDto(dto.id!!, it))

      result.add(item)
    }

    return result
  }

  fun getCurrentUserEmail(): String? = SecurityContextHolder.getContext().authentication.principal as String

  /*fun darOk(id: Long) : Boolean {
    val agendamento = agendamentoRepository.findById(id).get()
    var result: Boolean = false
    if (agendamento != null) {
      agendamento.agendamentoStatusAtual = AgendamentoStatus.OK
      val user = userRepository.findByUsername(getCurrentUserEmail()).get()

      val historicoAgendamentoStatus : HistoricoAgendamentoStatus = HistoricoAgendamentoStatus()
      historicoAgendamentoStatus.status = AgendamentoStatus.OK
      historicoAgendamentoStatus.createdById = user.id
      historicoAgendamentoStatus.dateCreated = LocalDateTime.now()
      historicoAgendamentoStatus.refAgendamento = agendamento

      historicoAgendamentoStatusRepository.save(historicoAgendamentoStatus)
      agendamentoRepository.save(agendamento)
      result = true
    }

    return result;
  }*/

  fun agendamentoAtual(id: Long) : AgendamentoCustomDto? {
    val agendamento = getAgendamentoAtual(id)
    if (agendamento != null) {
      var dtoResult = buildAgendamentoDto(id!!, agendamento)
      return AgendamentoCustomDto.fromDto(dtoResult!!)
    }
    return null
  }

  private fun getAgendamentoAtual(id: Long) : Agendamento? {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val fim = LocalDateTime.now(saoPauloZone)
    val inicio = fim.plusMinutes(30)

    val lstAgendamento = agendamentoRepository.findByDiaAtual(id, inicio, fim)

    if (lstAgendamento.isEmpty()){
      val final = fim.withHour(23).withMinute(59).withSecond(59)
      val list = agendamentoRepository.findByDiaRestante(id, inicio, final)
      return list.getOrNull(0)
    }

    return lstAgendamento.getOrNull(0)
   }

  fun agendamentosDoDia() : MutableList<AgendamentoCustomDto> {
    val lstAgendamento = getAgendamentosDoDia()

    var resultAgendamentos = mutableListOf<AgendamentoCustomDto>()
    lstAgendamento.forEach {

      var dtoResult = buildProfissionalAgendamentoDto(it.agendaSalas!!, it)
      resultAgendamentos.add(AgendamentoCustomDto.fromDto(dtoResult!!))
    }
    return resultAgendamentos
  }

  fun agendamentoDoDiaById(id: Long) : AgendamentoCustomDto {
    val lstAgendamento = getAgendamentosDoDiaById(id)
    var dtoResult = buildProfissionalAgendamentoDto(lstAgendamento.agendaSalas!!, lstAgendamento)
    var resultAgendamento = AgendamentoCustomDto.fromDto(dtoResult!!)

    return resultAgendamento
  }

  fun getAgendamentosDoDiaById(id: Long) : Agendamento {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val inicio = LocalDateTime.now(saoPauloZone).withHour(6).withMinute(0).withSecond(0)
    val fim = inicio.withHour(23).withMinute(59).withSecond(59)

    val agendamento = agendamentoRepository.findDiaAtualByIdAndInicioAndFim(id,inicio, fim)

      val agAss = agendaSalaRepository.findByAgendamento(agendamento.id!!)
      agendamento.agendaSalas = agAss.toMutableList()

    return agendamento
  }

  fun getAgendamentosDoDia() : List<Agendamento> {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val inicio = LocalDateTime.now(saoPauloZone).withHour(6).withMinute(0).withSecond(0)
    val fim = inicio.withHour(23).withMinute(59).withSecond(59)

    val lstAgendamento = agendamentoRepository.findDiaAtualByInicioAndFim(inicio, fim)

    lstAgendamento.forEach {
      val agAss = agendaSalaRepository.findByAgendamento(it.id!!)
      it.agendaSalas = agAss.toMutableList()
    }

    return lstAgendamento
  }


  fun agendamentoAtualProfessional(idProfissional: Long) : AgendamentoCustomDto? {
    val agendamento = getAgendamentoAtualProfissional(idProfissional)
    if (agendamento != null) {
      var dtoResult = buildProfissionalAgendamentoDto(agendamento.agendaSalas!!, agendamento)
      return AgendamentoCustomDto.fromDto(dtoResult!!)
    }
    return null
  }

  fun getAgendamentoAtualProfissional(id: Long) : Agendamento? {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val fim = LocalDateTime.now(saoPauloZone)
    val inicio = fim.plusMinutes(30)

    val lstAgendamento = agendamentoRepository.findByDiaAtualProfissional(id, inicio, fim)

    if (lstAgendamento.isEmpty()){
      val final = fim.withHour(23).withMinute(59).withSecond(59)
      val list = agendamentoRepository.findByDiaRestanteProfissional(id, inicio, final)
      return list.getOrNull(0)
    }

    var agendamento =  lstAgendamento.getOrNull(0)

    if (agendamento != null){
      val agAss = agendaSalaRepository.findByAgendamento(agendamento.id!!)
      agendamento.agendaSalas = agAss.toMutableList()
    }

    return agendamento
  }

  private fun buildAgendamentoDto(idAssistente: Long, it: Agendamento): AgendamentoDto {
    val agAss = agendaSalaRepository.findByAssistenteAgendamento(idAssistente, it.id)
    // it.refAgendaSalaEntities.(agAss)
    val agendaSala = agAss.get(0)
    val finalAgendaSala = agAss.last()
    val item = it.toDTO()
    item.assistente = agendaSala.assistente?.toCustomDTO()
    item.sala = agendaSala.sala
    item.inicio = agendaSala.inicio
    item.fim = finalAgendaSala.fim
    //item.horarioSalaOcupada = agendaSala. .toLocalDateTime()
    //item.horarioSalaLiberada = agendaSala.fim.toLocalDateTime()
    item.custoHora = agendaSala.custo
    item.tipoReserva = it.tipoReserva
    item.historicosAgendamentos =  historicoAgendamentoStatusRepository.findByIdAgendamento(it.id)
    item.tiposEquipamentos = arrayListOf()

    var tipos = mutableListOf<AgendaSalaTipoEquipamento>()
    agAss!!.forEach { ag ->
      val result = agendaSalaEquipamentoRepository.findAllByAgendaSalaAndNotCancelado(ag.id)
      if (result != null)
        tipos.addAll(result)
    }

    tipos.groupBy { it.tipoEquipamento }.forEach { tipoEquipamento, listAgendaSalaTipoEq ->
      var agendaSalaTipoEquipamento = AgendaSalaTipoEquipamento()
      agendaSalaTipoEquipamento.id = listAgendaSalaTipoEq.first().id
      agendaSalaTipoEquipamento.tipoEquipamento = tipoEquipamento

      val agendaSalas = listAgendaSalaTipoEq.map {it.agendaSala}
      var agendaSala = AgendaSala()
      agendaSala.inicio = agendaSalas.minBy { ag -> ag?.inicio!! }?.inicio
      agendaSala.fim = agendaSalas.maxBy { ag -> ag?.fim!! }?.fim
      agendaSala.custo = agendaSalas.first()?.custo

      agendaSalaTipoEquipamento.agendaSala = agendaSala

      item?.tiposEquipamentos?.add(agendaSalaTipoEquipamento)
    }

    return item
  }

  private fun buildProfissionalAgendamentoDto(agAss: MutableList<AgendaSala>, it: Agendamento): AgendamentoDto {
    // it.refAgendaSalaEntities.(agAss)
    val agendaSala = agAss.get(0)
    val finalAgendaSala = agAss.last()
    val item = it.toDTO()
    item.assistente = agendaSala.assistente?.toCustomDTO()
    item.sala = agendaSala.sala
    item.inicio = agendaSala.inicio
    item.fim = finalAgendaSala.fim
    // item.horarioSalaOcupada = agendaSala. .toLocalDateTime()
    //item.horarioSalaLiberada = agendaSala.fim.toLocalDateTime()
    item.custoHora = agendaSala.custo
    item.tipoReserva = it.tipoReserva
    item.tiposEquipamentos = arrayListOf()
    item.historicosAgendamentos =  historicoAgendamentoStatusRepository.findByIdAgendamento(it.id)

    var tipos = mutableListOf<AgendaSalaTipoEquipamento>()
    agAss!!.forEach { ag ->
      val result = agendaSalaEquipamentoRepository.findAllByAgendaSalaAndNotCancelado(ag.id)
      if (result != null)
        tipos.addAll(result)
    }

    tipos.groupBy { it.tipoEquipamento }.forEach { tipoEquipamento, listAgendaSalaTipoEq ->
      var agendaSalaTipoEquipamento = AgendaSalaTipoEquipamento()
      agendaSalaTipoEquipamento.id = listAgendaSalaTipoEq.first().id
      agendaSalaTipoEquipamento.tipoEquipamento = tipoEquipamento

      val agendaSalas = listAgendaSalaTipoEq.map {it.agendaSala}
      var agendaSala = AgendaSala()
      agendaSala.inicio = agendaSalas.minBy { ag -> ag?.inicio!! }?.inicio
      agendaSala.fim = agendaSalas.maxBy { ag -> ag?.fim!! }?.fim
      agendaSala.custo = agendaSalas.first()?.custo

      agendaSalaTipoEquipamento.agendaSala = agendaSala

      item?.tiposEquipamentos?.add(agendaSalaTipoEquipamento)
    }
    return item
  }

  fun save(dto: AgendaAssistenteDto): AgendaAssistente = agendaAssistenteRepository.saveAndFlush(AgendaAssistente.fromDTO(dto))

/*
  fun liberarSala(id: Long) : Boolean {
    val agendamento = agendamentoRepository.findById(id).get()
    var result: Boolean = false
    if (agendamento != null) {
      agendamento.agendamentoStatusAtual = AgendamentoStatus.LIBERADO
      val user = userRepository.findByUsername(getCurrentUserEmail()).get()
      val historicoAgendamentoStatus : HistoricoAgendamentoStatus = HistoricoAgendamentoStatus()
      historicoAgendamentoStatus.status = AgendamentoStatus.LIBERADO
      historicoAgendamentoStatus.createdById = user.id
      historicoAgendamentoStatus.dateCreated = LocalDateTime.now()
      val  principal = SecurityContextHolder.getContext().authentication
      historicoAgendamentoStatus.dateCreated = LocalDateTime.now()
      historicoAgendamentoStatus.refAgendamento = agendamento

      historicoAgendamentoStatusRepository.save(historicoAgendamentoStatus)
      agendamentoRepository.save(agendamento)
      result = true
    }

    return result;
  }
*/

  fun agendarAssistente(dto: AgendaAssistenteDto):MutableCollection<AgendaAssistente> {

    val horaInicio = dto.horaInicio
    val horaFim = dto.horaFim
    val diasDaSemana = dto.diasDaSemana
    var agendaAssistente: AgendaAssistente = AgendaAssistente()
    var agendaAssistenteLst: MutableCollection<AgendaAssistente> = arrayListOf()
    var agenda:  AgendaAssistenteDto = dto

    val sala = salaRepository.findById(dto.sala).get()
    val assistente = assistenteRepository.findById(dto.assistente).get()
    var i: Int = 0
    var agendaSala = mutableListOf<AgendaAssistente>()
    var horaInicioCheck = horaInicio
    var horafimCheck = horaFim
    for (dia in diasDaSemana!!) {
      var groupId:String = dia.toString()+'_'+horaInicio+'_'+horaFim



      if (agendaSala.isNotEmpty()) {
        return agendaAssistenteLst
      }

      if (agenda.horaInicio != null && agenda.horaFim != null) {
        for(i in agenda.horaInicio!! until agenda.horaFim!! step 1){
          if (agendamentoSalaService.validarHora(i, dia)) {
            agenda.refSalaEntity = sala
            agenda.assistenteEntity = assistente
            agenda.groupId =groupId
            agenda.diaDaSemana = dia
            agenda.horaDoDia = i
            agenda.version = 1


            agendaAssistente =  save(agenda)
            agendaAssistenteLst.add(agendaAssistente)

          }
        }
      }
      i++
    }
    return agendaAssistenteLst
  }


    fun getDiasIndisponiveis(sala:Long, assistente: Long, horaInicio: Int, horaFim: Int): List<Int> {

      var indisponiveis = mutableListOf<AgendaAssistente>()
      var agendas = agendaAssistenteRepository.findBySalaId(sala)
      var ret: MutableCollection<AgendaAssistente>? = arrayListOf()

      for (i in horaInicio!! until horaFim!! step 1) {
         indisponiveis?.addAll(agendas.filter { it.horaDoDia == i && it.assistente?.id != assistente })
      }
      var diasIndisponiveis = listOf<Int>()
      if (indisponiveis != null)
          diasIndisponiveis = indisponiveis.map { it.diaDaSemana }.filterNotNull().distinct()

      return  diasIndisponiveis.map { it }
    }





  fun getAgendasAssistenteByAssistente(asssistenteId: Long) : MutableList<AgendaAssistenteDataDto> {

    val assistente = assistenteRepository.findById(asssistenteId).get()

    val agendaAssistente = agendaAssistenteRepository.findByAssistente(assistente)

    var result : MutableList<AgendaAssistenteDataDto> = mutableListOf()

    if(!agendaAssistente.isEmpty()) {
      var agendasByGroupId =  agendaAssistente.groupBy({it.groupId}).forEach { groupId, agendaAss ->
        var horaInicio =  agendaAss.first().horaDoDia
        var horaFim =  agendaAss.last().horaDoDia

        var item = AgendaAssistenteDataDto()
        item.groupId = groupId
        item.assistente = assistente.toCustomDTO()
        item.idSala = agendaAss[0].sala?.id
        item.nomeSala = agendaAss[0].sala?.nome
        item.horaInicio = horaInicio
        item.horaFim = horaFim
        item.diaDaSemana = agendaAss[0].diaDaSemana

        result.add(item)

      }

    }
    return result
  }

  fun excluirAgendaAssistente(requestData: ExcluirAgendamentoAssistenteDto)  {
      val horaInicio = requestData.horaInicio
      val diaDaSemana = requestData.diaDaSemana
      val assistente = assistenteRepository.findById(requestData.assistente).get()

      val agendaAssistente = agendaAssistenteRepository.findByAssistente(assistente)


      val agenda = agendaAssistente.find { it.diaDaSemana == diaDaSemana && it.horaDoDia == horaInicio }
      val toRemove = agendaAssistente.filter { it.groupId == agenda?.groupId }

    if(agenda?.sala?.id == null){
      return
    }
      val sala = salaRepository.findById(agenda?.sala?.id)

      toRemove.forEach{
        agendaAssistenteRepository.delete(it)
      }

  }

}



