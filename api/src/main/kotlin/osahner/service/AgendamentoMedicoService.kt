package osahner.service

import org.apache.poi.ss.usermodel.Workbook
import org.hibernate.Hibernate
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import osahner.dto.*
import osahner.enums.*
import osahner.repository.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class AgendamentoMedicoService (
  private val agendamentoSalaRepository: AgendaSalaRepository,
  private val agendamentoRepository: AgendamentoRepository,
  private val userRepository: UserRepository,
  private val medicoRepository: MedicoRepository,
  private val userService: UserService,
  private val faturaService: FaturaService,
  private val agendaSalaEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
  private val creditoRepository: CreditoRepository,
  private val salaRepository: SalaRepository,
  private val poiExportService: PoiExportService,
  private val historicoAgendamentoStatusRepository: HistoricoAgendamentoStatusRepository,
  private val medicoService: MedicoService,
  private val auditLogService: AuditLogService,
  private val consultaService: ConsultaService

) {
  var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")

  val prazoCancelamento: Long = 48

  private fun getCurrentUserEmail(): String? = SecurityContextHolder.getContext().authentication.principal as String

  fun listarAgendamentosMedico(medicoId : Long, inicio: String, fim: String): MutableList<AgendamentoListarMedicoDto>{

    val  dataInicio = LocalDateTime.parse(inicio,DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val  dataFim = LocalDateTime.parse(fim,DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    var agendamentoDTOS : MutableList<AgendamentoListarMedicoDto> = arrayListOf()
   // var agendamentos: MutableList<Agendamento> =  agendamentoRepository.findAllByMedicoAndStatus(medicoId,SolicitacaoStatus.CONFIRMADO)

    var agendamentos: MutableList<Agendamento> = agendamentoRepository.findAllByMedicoAndInicioMesAndFimMes(medicoId,dataInicio,dataFim)
      agendamentos.forEach {agendamento ->

        //Hibernate.initialize(agendamento.agendaSalas?.get(0)?.sala)

        var agendamentoDTO =  agendamento?.toDTO()
        agendamentoDTO.sala = agendamento.agendaSalas?.first()?.sala
        agendamentoDTO.assistente = agendamento.agendaSalas?.first()?.assistente!!.toCustomDTO()
        var minDate = agendamento.agendaSalas?.minBy {it.inicio!!}?.inicio
        var maxDate = agendamento.agendaSalas?.maxBy {it.fim!!}?.fim

        var agendamentoListarDto = AgendamentoListarMedicoDto(
          id = agendamento.id,
          version = agendamento.version,
          dia = agendamento.dia,
          dateCreated =agendamento.dateCreated,
          custo = agendamento.custo,
          agendamentoStatusAtual = agendamento.agendamentoStatusAtual,
          lastUpdated = agendamento.lastUpdated,
          status = agendamento.status,
          periodo = agendamento.periodo,
          user = agendamento.medico?.toCustomDTO(),
          fatura = agendamento.refFaturaEntity,
          sala = agendamento.agendaSalas?.get(0)?.sala,
          assistente = agendamento.agendaSalas?.first()?.assistente?.toCustomDTO(),
          inicio = minDate,
          fim=maxDate,
          tipoReserva = agendamento.tipoReserva
        )

        var tipos = mutableListOf<AgendaSalaTipoEquipamento>()
        agendamento.agendaSalas!!.forEach {
          val tiposEquipamentos = agendaSalaEquipamentoRepository.findAllByAgendamentoAndStatus(it.id)
          if (tiposEquipamentos != null)
            tipos.addAll(tiposEquipamentos)
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

          agendamentoListarDto?.tiposEquipamentos?.add(agendaSalaTipoEquipamento.toCustomDto())
        }

        agendamentoDTOS.add(agendamentoListarDto)
    }
      return agendamentoDTOS
  }

  fun listarAgendamentos (salaId:Long, inicio: String, fim:String) : MutableList<AgendamentoCustomDto> {
    val sala = salaRepository.findById(salaId).get()

    val  dataInicio = LocalDateTime.parse(inicio,DateTimeFormatter.ISO_DATE_TIME)
    val  dataFim = LocalDateTime.parse(fim,DateTimeFormatter.ISO_DATE_TIME)

    var reservas = agendamentoSalaRepository.findAllBySalaAndInicioAndFim(sala.id, dataInicio, dataFim)

    var salasAlocadasMedico = reservas!!.filter { it.agendamento != null }
    var salasAlocadasManutencao = reservas!!.filter { it.manutencao != null }

    var agendamentoDTOS : MutableList<AgendamentoCustomDto> = arrayListOf()

    salasAlocadasMedico.filter{ it.agendamento?.status != SolicitacaoStatus.CANCELADO }.groupBy { it.agendamento }.forEach{ ag, agenda ->
      var agendamentoDTO =  ag?.toDTO()
      agendamentoDTO?.sala = agenda.first().sala
      agendamentoDTO?.assistente = agenda.first().assistente?.toCustomDTO()
      val dataInicio = agenda.minBy {it.inicio!!}?.inicio
      val dataFim = agenda.maxBy {it.fim!!}?.fim

      agendamentoDTO?.inicio = dataInicio
      agendamentoDTO?.fim = dataFim

      agendamentoDTO?.tipoReserva = ag?.tipoReserva

      var tipos = mutableListOf<AgendaSalaTipoEquipamento>()
      agenda.forEach {
        val tiposEquipamentos = agendaSalaEquipamentoRepository.findAllByAgendaSalaAndNotCancelado(it.id)
        if (tiposEquipamentos != null)
          tipos.addAll(tiposEquipamentos)
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

        agendamentoDTO?.tiposEquipamentos?.add(agendaSalaTipoEquipamento)
      }

      agendamentoDTO?.historicosAgendamentos = historicoAgendamentoStatusRepository.findByIdAgendamento(ag?.id)// ag?.historicoAgendamentoStatus

      agendamentoDTOS.add(AgendamentoCustomDto.fromDto(agendamentoDTO!!))
    }

    salasAlocadasManutencao.filter { it.manutencao != null }.groupBy { it.agendamento }.forEach{  manutencao, agenda ->
      var agendamentoDTO =  agenda.first().agendamento?.toDTO()

      agendamentoDTO?.sala = agenda.first().sala
      val dataInicio = agenda.minBy {it.inicio!!}?.inicio
      val dataFim = agenda.maxBy {it.fim!!}?.fim

      agendamentoDTO?.inicio = dataInicio
      agendamentoDTO?.fim = dataFim
      agendamentoDTO?.tipoReserva = TipoReserva.MANUTENCAO

      agendamentoDTOS.add(AgendamentoCustomDto.fromDto(agendamentoDTO!!))
    }
    return agendamentoDTOS
  }

  fun alterarAssistente(agendamento: Agendamento, assistente: Assistente) {
    agendamento.agendaSalas?.forEach {
      it.assistente = assistente
    }
    agendamentoSalaRepository.saveAll(agendamento.agendaSalas)
  }

  @Transactional
  fun cancelar(dto: CancelarAgendamentoDto): ResumoCancelamentoDto {

    val agendamento = agendamentoRepository.findById(dto.agendamento).get()

    if (agendamento.agendamentoStatusAtual != AgendamentoStatus.NAO_INICIADO) {
        throw  RuntimeException("Agendamento não pode ser cancelado, pois já foi iniciado")
      }

      if (agendamento.status == SolicitacaoStatus.CANCELADO) {
        throw  RuntimeException("Agendamento já cancelado anteriormente")
      }

      val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
      val dataAgendamento = agendamento.agendaSalas?.first()?.inicio
      val dataMinimaCancelamento = LocalDateTime.now(saoPauloZone).plusHours(prazoCancelamento)

      if (dataAgendamento!!.isBefore(dataMinimaCancelamento) || dataAgendamento!!.isEqual(dataMinimaCancelamento)) {
        throw RuntimeException("Não é possível cancelar agendamentos com data de agendamento inferior a 48h")
      }

      val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
      val dia = formatterData.format(agendamento.dia)

      val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
      val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
      val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)

      var username = userService.getCurrentUserEmail()
      var currentUser = userService.findByUsername(username).get()

      val descricaoLog = "Agendamento do profissional "+ agendamento.medico?.user?.nome +
              " cancelado para sala '" + agendamento.agendaSalas?.first()?.sala?.nome + "' no dia " + dia + " das " + inicio + " às " + fim


      val resumo1 = faturaService.gerarCreditoOuCancelamento(agendamento)
      val resumo2 = faturaService.aplicarCreditosExistentes(agendamento.medico!!)

      this.cancelarAgendamento(agendamento)

      auditLogService.criarLog(currentUser, LogEnum.APAGAR_AGENDAMENTO, descricaoLog)


      val resumoCancelamentoDTO = ResumoCancelamentoDto()
      resumoCancelamentoDTO.creditoRecebido = resumo1.creditoGanho
      resumoCancelamentoDTO.creditoUtilizado = resumo2?.creditoUtilizado
      resumoCancelamentoDTO.fatura = resumo1.fatura?.toDTO()
      resumoCancelamentoDTO.antesAbatimento = if (resumo1.creditoRecuperado == resumo1.abatimento) 0.0 else resumo1.fatura?.valor!! + resumo1.abatimento!!

      return resumoCancelamentoDTO
    }

  private fun cancelarAgendamento(agendamento : Agendamento) {

    agendamento.status = SolicitacaoStatus.CANCELADO
    agendamento.agendaSalas?.forEach {
      it.agendaSalaTipoEquipamentos?.filter { it.status != SolicitacaoStatus.CANCELADO }?.forEach {
        it.status = SolicitacaoStatus.CANCELADO
      }
    }
    var agendaSalas =  agendamento.agendaSalas

    agendamento.agendaSalas = mutableListOf()

    agendamentoRepository.save(agendamento)

    agendaSalas?.forEach {
      it.agendamento = null
      it.custo = 0.0
    }
    agendamentoSalaRepository.saveAll(agendaSalas)

    // Cancela as consultas médicas amarradas ao agendamento
    consultaService.findByAgendamentoId(agendamento.id!!).forEach {
      consultaService.cancelAppointment(it.id!!)
    }
  }

  fun proximoAgendamentoMedico(medico: Long): MedicoAgendamentoDto {

    val medico = medicoRepository.findById(medico).get()

    val agendamentos = agendamentoRepository.findByMedicoAndStatusAndAgendamentoStatusAtual(medico, SolicitacaoStatus.CONFIRMADO, AgendamentoStatus.NAO_INICIADO)
    var agendaSalas : MutableList<AgendaSala> = mutableListOf()
    agendamentos.forEach{
      agendaSalas.addAll(it.agendaSalas!!)
    }

    val result = MedicoAgendamentoDto()
    if(!agendaSalas.isEmpty()) {
      val agendaSalasTmp = agendaSalas.sortedBy {it.inicio}.toMutableList()
      val agendaSalasReversaTmp = agendaSalas.sortedBy {it.fim}.toMutableList()
      var agendamento = agendaSalasTmp.get(0).agendamento
      result.sala = agendaSalasTmp.get(0).sala
      result.inicio = agendaSalasTmp.get(0).inicio.toString()
      result.fim = agendaSalasReversaTmp.get(0).fim.toString()
    }
    return result

  }


   fun pesquisarAgendamentosMedico(professionalId: String?, cpf: String?, inicio: LocalDateTime, fim: LocalDateTime ):MutableList<AgendamentoGetDto> {
      var agendamentos: MutableList<Agendamento>? = mutableListOf()
      var agendamentosDto: MutableList<AgendamentoGetDto> = mutableListOf()
      var medico: Medico

    if(professionalId != "" ) {
    //  medico =  medicoRepository.findById(medicoId).get()
      var medicoId = professionalId?.toLong()
      agendamentos = agendamentoRepository.findAllByMedicoAndInicioAndFim(medicoId, inicio.with(LocalTime.of(0,0)),fim.with(LocalTime.of(23,59)))

    } else if(professionalId == "" && cpf != "") {

      agendamentos = agendamentoRepository.findAllByMedicoCpfAndInicioAndFim(cpf, inicio.with(LocalTime.of(0,0)),fim.with(LocalTime.of(23,59)))

    } else{
      agendamentos = agendamentoRepository.findAllByInicioAndFim(inicio.with(LocalTime.of(0,0)),fim.with(LocalTime.of(23,59)))
    }

     agendamentos.forEach{agendamento ->
        var agendamentoDto: AgendamentoGetDto = AgendamentoGetDto(null,null,null, null, null, null, null, null, null, null, null,null , null, null, null, null, null, null)
          agendamentoDto.agendamento = agendamento
          agendamentoDto.status =  agendamento.status
          agendamentoDto.sala =  agendamento.agendaSalas?.first()?.sala
          agendamentoDto.assistente = agendamento.agendaSalas?.first()?.assistente
          agendamentoDto.profissional = agendamento.medico
          agendamentoDto.inicio = agendamento.agendaSalas?.first()?.inicio
          agendamentoDto.fim = agendamento.agendaSalas?.last()?.fim
          agendamentoDto.custoHora = agendamento.agendaSalas?.first()?.custo
          agendamentoDto.nomeProfissional = agendamento.medico?.user?.nome
          agendamentoDto.nomeAssistente =   agendamento.agendaSalas?.first()?.assistente?.user?.nome


        var historicoAgendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(agendamento.id)

       historicoAgendamento.forEach {
         if(it?.status == AgendamentoStatus.OK && it.refUserEntity?.id == agendamento.medico?.user?.id) {
           agendamentoDto.checkinProfessional = it?.dateCreated
           //agendamentoDto.horarioSalaOcupada = it?.dateCreated
         }else if(it?.status == AgendamentoStatus.OK && it.refUserEntity?.id == agendamento.agendaSalas?.first()?.assistente?.user?.id) {
           agendamentoDto.checkinAssistente = it?.dateCreated
           //agendamentoDto.horarioSalaOcupada = it?.dateCreated
         }


         if(it?.status == AgendamentoStatus.LIBERADO && it.refUserEntity?.id == agendamento.medico?.user?.id) {
           agendamentoDto.checkoutProfessional = it?.dateCreated
           //agendamentoDto.horarioSalaOcupada = it?.dateCreated
         }else if(it?.status == AgendamentoStatus.LIBERADO && it.refUserEntity?.id == agendamento.agendaSalas?.first()?.assistente?.user?.id) {
           agendamentoDto.checkoutAssitente = it?.dateCreated
           //agendamentoDto.horarioSalaOcupada = it?.dateCreated
         }

       }

       agendamentosDto.add(agendamentoDto)
     }
     agendamentosDto.sortBy { it.inicio }
     return agendamentosDto
   }



  fun toWorkbook(professionalId: String?, cpf: String?, inicio: LocalDateTime, fim: LocalDateTime ): Workbook {
    var dtos: MutableList<AddressDto> = mutableListOf()
    val result = this.pesquisarAgendamentosMedico(professionalId, cpf, inicio, fim)
    var list: MutableList<String> = mutableListOf()
    var listRet: MutableList<MutableList<String>> = mutableListOf()
    val format = SimpleDateFormat("dd/MM/yyy")
    result.forEach {
      var dto: AddressDto = AddressDto(null, null, null, null, null, null, null, null, null, null)


      dto.data = it.agendamento?.dia?.dayOfMonth.toString() +"/"+ it.agendamento?.dia?.monthValue?.toString()+"/"+ it.agendamento?.dia?.year.toString()
      dto.horaInicio = it.inicio?.hour
      dto.horaFim = it.fim?.hour
      dto.valorPago = it.custoHora
      dto.assistente = it.assistente?.user?.nome
      dto.profissional = it.profissional?.user?.nome
      if(it.checkinAssistente == null){dto.checkinAssistente = ""}else{ dto.checkinAssistente= it.checkinAssistente?.dayOfMonth.toString() +"/"+ it.checkinAssistente?.monthValue?.toString()+"/"+ it.checkinAssistente?.year.toString()+" "+ it.checkinAssistente?.hour.toString()+":"+ it.checkinAssistente?.minute.toString() }
      if(it.checkinProfessional == null){dto.checkinProfessional = ""}else{ dto.checkinProfessional= it.checkinProfessional?.dayOfMonth.toString() +"/"+ it.checkinProfessional?.monthValue?.toString()+"/"+ it.checkinProfessional?.year.toString()+" "+ it.checkinProfessional?.hour.toString()+":"+ it.checkinProfessional?.minute.toString()}
      if(it.checkoutAssitente == null){dto.checkoutAssitente = ""}else{ dto.checkoutAssitente= it.checkoutAssitente?.dayOfMonth.toString() +"/"+ it.checkoutAssitente?.monthValue?.toString()+"/"+ it.checkoutAssitente?.year.toString()+" "+ it.checkoutAssitente?.hour.toString()+":"+ it.checkoutAssitente?.minute.toString()}
      if(it.checkoutProfessional == null){dto.checkoutProfessional = ""}else{ dto.checkoutProfessional= it.checkoutProfessional?.dayOfMonth.toString() +"/"+ it.checkoutProfessional?.monthValue?.toString()+"/"+ it.checkoutProfessional?.year.toString()+" "+ it.checkoutProfessional?.hour.toString()+":"+ it.checkoutProfessional?.minute.toString()}

      dtos.add(dto)

  }

    var nome = result.get(0).profissional?.user?.nome
    var cpf = cpf
    var dataInicio = inicio
    var dataFim = fim
    return poiExportService.buildExcelDocument(
      "Export Address List",
      nome, cpf, dataInicio, dataFim,
      listOf("data", "horaInicio", "horaFim", "valorPago", "assistente", "profissional", "checkinAssistente", "checkinProfessional","checkoutAssitente","checkoutProfessional"),
      listOf("data", "Hora Inícios", "Hora Fim", "Valor Pago/Hora", "Assistente", "Profissional", "Checkin Assistente", "Checkin Profissional", "Checkout Assistente", "Checkout Profissional"),
      dtos
    )
  }

  @Transactional
  fun getCredito(idProfissional: Long): Double {
    val medico = medicoService.findById(idProfissional).get()

    if (medico == null)
      ResponseEntity.notFound().build<Medico>()

    val creditos = creditoRepository.findAByValorDisponivelGreaterThanAndMedico(0.0, medico)
    return creditos.sumByDouble { it.valorDisponivel!! } ?: 0.0
  }
}








