package osahner.controller

import org.apache.poi.ss.usermodel.Workbook
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.domain.*
import osahner.dto.*
import osahner.enums.AgendamentoStatus
import osahner.enums.LogEnum
import osahner.exception.AgendamentoVazioException
import osahner.repository.CarrinhoRepository
import osahner.repository.MedicoRepository
import osahner.repository.UserRepository
import osahner.service.*
import osahner.utils.Utils
import java.io.ByteArrayOutputStream
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*


@RestController
@CrossOrigin
//@Api(value = "admin", description = "Rest API for administrative operations", tags = arrayOf("Admin API"))
@RequestMapping("/scheduling")
class AgendamentoController(private val agendamentoService: AgendamentoService,
                            private val agendamentoAssistenteService: AgendamentoAssistenteService,
                            private val agendamentoSalaService: AgendamentoSalaService,
                            private val agendamentoMedicoService: AgendamentoMedicoService,
                            private val carrinhoService: CarrinhoService,
                            private val solicitacaoEquipamentoService: SolicitacaoEquipamentoService,
                            private val assistenteService: AssistenteService,
                            private val salaService: SalaService,
                            private val securityProperties: SecurityProperties,
                            private val historicoAgendamentoStatusService: HistoricoAgendamentoStatusService,
                            private val medicoRepository: MedicoRepository,
                            private val userRepository: UserRepository,
                            private val carrinhoRepository: CarrinhoRepository,
                            private val auditLogService: AuditLogService,
                            private val poiExportService: PoiExportService
                           ) {


  @PostMapping(value = ["/agendarAssistente"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
  fun agendarAssistente(@RequestBody dto: AgendaAssistenteDto):Any{
    var ret  = agendamentoAssistenteService.agendarAssistente(dto)
    if (ret.isEmpty()){
      return ResponseEntity.badRequest().body("Já existe assistente agendado nesta sala")
    }

    return ret
  }

  @GetMapping(value = ["/listarSalasDisponiveis"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun listarSalasDisponiveis() = agendamentoSalaService.listarSalasDisponiveis()

  @GetMapping(value = ["/listarTiposEquipamentosDisponiveis"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun listarTiposEquipamentosDisponiveis(@RequestParam inicio: String, fim:String): MutableList<EquipamentosDisponiveisDto?> {
    val dataInicio: LocalDateTime  = LocalDateTime.parse(inicio, ISO_DATE_TIME)
    val DataFim: LocalDateTime  = LocalDateTime.parse(fim, ISO_DATE_TIME)

    val ret = solicitacaoEquipamentoService.listarTiposEquipamentosDisponiveis(dataInicio, DataFim)

    return ret
  }

  @GetMapping(value = ["/listarAgendamentos"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE','ROLE_ATENDENTE','ROLE_PROFISSIONAL')") // iniciado e nao terminado duvidas
  fun listarAgendamentos(@RequestParam sala: Long, @RequestParam inicio: String, @RequestParam  fim:String) = agendamentoMedicoService.listarAgendamentos(sala,inicio, fim)


  @GetMapping(value = ["/sala"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE','ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun sala(@RequestParam sala: Long, @RequestParam inicio: String, @RequestParam  fim: String): Any? {
    val sala = salaService.findById(sala)
    val inicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_DATE)
    val fim = LocalDate.parse (fim, DateTimeFormatter.ISO_DATE)

    if(sala == null)
      return ResponseEntity.notFound().build<Sala>()

    return agendamentoSalaService.getAgendaCompleta(sala.get(), inicio, fim)
  }

  @GetMapping(value = ["/horariosAssistentes"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE','ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun horariosAssistentes(@RequestParam sala: Long, @RequestParam inicio: String, @RequestParam  fim: String): Any? {
    val sala = salaService.findById(sala)
    val inicio = LocalDate.parse(inicio, DateTimeFormatter.ISO_DATE)
    val fim = LocalDate.parse (fim, DateTimeFormatter.ISO_DATE)

    if(sala == null)
      return ResponseEntity.notFound().build<Sala>()

    val agendaCompleta = agendamentoSalaService.getAgendaCompleta(sala.get(), inicio, fim)

    return agendamentoSalaService.getHorariosAssistentes(agendaCompleta!!)
  }

  @GetMapping(value = ["/listarAgendamentosMedico"])
  @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL', 'ROLE_PACIENTE', 'ROLE_ADMINISTRADOR','ROLE_ATENDENTE')")
  fun listarAgendamentosMedico(@RequestParam medico: Long, @RequestParam inicio: String, @RequestParam fim: String) = agendamentoMedicoService.listarAgendamentosMedico(medico, inicio, fim)


  @GetMapping(value = ["/pesquisar-agendamento-profissional"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun pesquisarAgendamentosMedico(@RequestParam professionalId: String, cpf: String, startDate: String, endDate: String): Any? {

    val dataInicio: LocalDateTime  = LocalDateTime.parse(startDate, ISO_LOCAL_DATE_TIME)
    val dataFim: LocalDateTime  = LocalDateTime.parse(endDate, ISO_LOCAL_DATE_TIME)

    return ResponseEntity.ok(agendamentoMedicoService.pesquisarAgendamentosMedico(professionalId, cpf, dataInicio, dataFim))
  }

  @DeleteMapping(value = ["/apagarReservas"])
  @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun apagarReservas(): ResponseEntity<Unit> {
    var ret = carrinhoService.cancelarPorProfissionalLogado()
   if(ret == false){
     return ResponseEntity.ok().build()
   }
    return ResponseEntity.ok().build()
  }

  @DeleteMapping(value = ["/apagarReservasPorProfissional/{idProfissional}"])
  @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL', 'ROLE_ATENDENTE', 'ROLE_ADMINISTRADOR')")
  fun apagarReservasPorProfissional(@PathVariable idProfissional: Long): ResponseEntity<Unit> {
    var ret = carrinhoService.cancelarPorIdProfissional(idProfissional)
    if(ret == false){
      return ResponseEntity.ok().build()
    }
    return ResponseEntity.ok().build()
  }

  @PostMapping(value = ["/cancelarAgendamento"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun cancelarAgendamento(@RequestBody dto: CancelarAgendamentoDto) : ResumoCancelamentoDto {

    val result = agendamentoMedicoService.cancelar(dto)

    return result
  }

  @PostMapping(value = ["/cancelarEquipamentos"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE','ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun cancelarEquipamentos(@RequestBody dto: CancelarEquipamentoDto) : ResponseEntity<ResumoCancelamentoDto> {
    val listHistorico = historicoAgendamentoStatusService.findByIdAgendamento(dto.agendamento)
    if (listHistorico.isNotEmpty())
      return throw RuntimeException("Não é possível cancelar um equipamento de um agendamento já iniciado")

    val result = carrinhoService.cancelarSolicitacaoEquipamento(dto)
    val response = ResponseEntity(result, HttpStatus.OK);
    return response
  }

  @PostMapping(value = ["/agendarAssistenteEmSala"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun agendarAssistenteEmSala(@RequestBody requestData: AgendaAssistenteRequestDto): Any? {

    if (requestData == null)
      return ResponseEntity.notFound().build<AgendaAssistenteRequestDto>()

    agendamentoSalaService.agendarAssistente(requestData)
    return ResponseEntity.ok().build<AgendaAssistenteRequestDto>()
  }

  @PutMapping(value = ["/alterarAssistente"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun alterarAssistente(@RequestBody dto: AlterarAssistenteDto): Any? {
    val agendamento = agendamentoService.findById(dto.agendamento?.id)
    val assistente = assistenteService.findById(dto.assistente?.id)

    if (agendamento == null|| assistente == null)
      return ResponseEntity.notFound().build<AlterarAssistenteDto>()

    agendamentoMedicoService.alterarAssistente(agendamento.get(), assistente.get())
    return ResponseEntity.ok().build<AlterarAssistenteDto>()
  }

  @GetMapping(value = ["/listarEquipamentos"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ASSISTENTE','ROLE_ATENDENTE')")
  fun listarEquipamentos(@RequestParam agendamento:Long) = solicitacaoEquipamentoService.listarEquipamentos(agendamento)

  @GetMapping(value = ["/proximoAgendamentoMedico"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL')")
  fun proximoAgendamentoMedico(@RequestParam medico:Long) = agendamentoMedicoService.proximoAgendamentoMedico(medico)

  @PostMapping(value = ["/excluirAgendaAssistente"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun excluirAgendaAssistente(@RequestBody dto: ExcluirAgendamentoAssistenteDto) : ResponseEntity<Unit> {
    agendamentoAssistenteService.excluirAgendaAssistente(dto)
    return ResponseEntity.ok().build()
  }

  @GetMapping(value = ["/listarAgendaAssistente"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun listarAgendaAssistente(@RequestParam assistente: Long) = agendamentoAssistenteService.getAgendasAssistenteByAssistente(assistente)

  @GetMapping(value = ["/diasIndisponiveis"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun diasIndisponiveis(@RequestParam sala: Long, assistente: Long, horaInicio:Int, horaFim:Int) = agendamentoAssistenteService.getDiasIndisponiveis(sala, assistente, horaInicio, horaFim)?.map { it }

  @PostMapping(value = ["/reservar"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL','ROLE_ATENDENTE')")
  fun reservar(@RequestBody dto: AgendamentoRequestDto) : SolicitacaoAgendamentoDto? {
    var result : SolicitacaoAgendamentoDto? = null
    try {
       result =  carrinhoService.reservar(dto)
    } catch (e: AgendamentoVazioException) {
      result = e.extraData
    }
    return result
  }

  @PostMapping(value = ["/agendarManutencao"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
  fun agendarManutencao(@RequestBody dto: ManutencaoDto) : Any? {
      var result : Any? = null
      try {
          result =  agendamentoSalaService.agendarManutencao(dto)
      } catch (e: AgendamentoVazioException) {
          result = e.extraData
      }
      return result
  }


  @PostMapping(value = ["/cancelarSolicitacaoAgendamento"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun cancelarSolicitacaoAgendamento(@RequestBody dto: CancelarAgendamentoDto) : ResponseEntity<Exception>? {
    var response : ResponseEntity<Exception>? = null

    val result = carrinhoService.cancelarSolicitacaoAgendamento(dto)
    if (result) {
      response =  ResponseEntity.ok().build()
    }

    return response
  }

  @PostMapping(value = ["/adicionarEquipamentos"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun adicionarEquipamentos(@RequestBody dto : AgendamentoEquipamentoRequestDto) : ResponseEntity<SolicitacaoEquipamentoDto> {
    val result = carrinhoService.adicionarEquipamentos(dto)
    if (!result.erros.isEmpty()) {
      return ResponseEntity(result, HttpStatus.BAD_REQUEST);
    } else {
      return ResponseEntity(result, HttpStatus.OK);
    }
  }

  @PostMapping(value = ["/finalizarCarrinho"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun finalizarCarrinho(@RequestBody dto: CartaoCreditoDto) : ResponseEntity<Exception>? {
    var response : ResponseEntity<Exception>? = null

    val user = userRepository.findByUsername(carrinhoService.getCurrentUserEmail()).get()
    val carrinho = carrinhoRepository.findById(dto.carrinho).get()

    var medico: Medico
    if (dto.isAtendente)
      medico = carrinho.medico!!
    else
      medico = medicoRepository.findByUser(user).get()

    if (medico?.logradouro == null || medico?.telefone == null || medico?.cep == null)
      throw RuntimeException("É necessário preencher os dados de Logradouro, CEP e Telefone no seu perfil de usuário para realizar uma compra")

    if (!Utils.checkCpf(dto.holderCpf!!))
      throw java.lang.RuntimeException("CPF Inválido")

    carrinhoService.finalizarCarrinho(dto, user, medico, carrinho )
    response =  ResponseEntity.ok().build()

    return response
  }

    @GetMapping(value = ["/export-agendamento-profissional"])
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun export(@RequestParam professionalId: String?, cpf: String?, startDate: String, endDate: String): ResponseEntity<ByteArrayResource> {
    val headers = HttpHeaders().apply {
      add("Content-Disposition", "filename=\"Export-${Date().time}.xls\"")
    }
    val dataInicio: LocalDateTime  = LocalDateTime.parse(startDate, ISO_LOCAL_DATE_TIME)
    val dataFim: LocalDateTime  = LocalDateTime.parse(endDate, ISO_LOCAL_DATE_TIME)
    val bos = ByteArrayOutputStream()
    bos.use {
      agendamentoMedicoService.toWorkbook(professionalId, cpf, dataInicio, dataFim).apply {
        write(bos)
      }
    }
    val resource = ByteArrayResource(bos.toByteArray())
    return ResponseEntity.ok()
      .headers(headers)
      .contentLength(resource.contentLength())
      .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
      .body(resource)
  }

  @PostMapping(value = ["/alterarAgendamento"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ASSISTENTE', 'ROLE_ATENDENTE','ROLE_PROFISSIONAL')")
  fun alterarAgendamento(@RequestBody dto: AlterarAgendamentoDto) : Any? {
    var response : ResponseEntity<Any>? = null
    var result : SolicitacaoAgendamentoDto? = null

    result = carrinhoService.alterarAgendamento(dto)
    response =  ResponseEntity(result, HttpStatus.OK)

    return response
  }

  @GetMapping(value = ["/creditos/{idProfissional}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun getCreditoAtual(@PathVariable idProfissional: Long) = agendamentoMedicoService.getCredito(idProfissional)

  @GetMapping(value = ["/schedules-of-day"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun agendamentosDoDia(): Any? {
    return ResponseEntity.ok(agendamentoAssistenteService.agendamentosDoDia())
  }

  @GetMapping(value = ["/schedules-of-day/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun agendamentosDoDiaById(@PathVariable id: Long): Any? {
    return ResponseEntity.ok(agendamentoAssistenteService.agendamentoDoDiaById(id))
  }

  @GetMapping(value = ["/export-schedules-of-day"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun export(): ResponseEntity<ByteArrayResource> {
    val headers = HttpHeaders().apply {
      add("Content-Disposition", "filename=\"Export-${Date().time}.xls\"")
    }

    val bos = ByteArrayOutputStream()
    bos.use {
      this.toWorkbook().apply {
        write(bos)
      }
    }
    val resource = ByteArrayResource(bos.toByteArray())
    return ResponseEntity.ok()
            .headers(headers)
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .body(resource)
  }

  fun toWorkbook(): Workbook {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val diaAtual = LocalDateTime.now(saoPauloZone)
    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val formatterData = DateTimeFormatter.ofPattern("dd-MM-uuuu")
    val dia = formatterData.format(diaAtual)

    val listDto = mutableListOf<AgendamentoDiaDto>()

    val result = agendamentoAssistenteService.agendamentosDoDia()
    result.forEach {

      val dto = AgendamentoDiaDto(
          nomeSala = it.sala?.nome,
          inicio = formatterHora.format(it.inicio),
          fim = formatterHora.format(it.fim),
          nomeAssistente = it.assistente?.nome,
          profissional = it.user?.nome,
          especialidade = it.user?.especialidade?.nome
      )

      listDto.add(dto)
    }

    return poiExportService.buildExcelDocumentAgendamentosDoDia(
        "Export Agendamentos do dia (${dia})",
        listOf("nomeSala", "inicio", "fim", "nomeAssistente", "profissional", "especialidade"),
        listOf("Nome da Sala", "Início", "Fim", "Assistente", "Profissional", "Especialidade"),
        listDto
    )
  }
}


