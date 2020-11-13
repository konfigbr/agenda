package osahner.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import osahner.dto.*
import osahner.enums.*
import osahner.exception.AgendamentoVazioException
import osahner.exception.ErroRealocarAgendamentoException
import osahner.repository.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*


@Component
@Transactional
class CarrinhoService(
  private val carrinhoRepository: CarrinhoRepository,
  private val assistenteRepository: AssistenteRepository,
  private val solicitacaoReservaRepository: SolicitacaoReservaRepository,
  private val solicitacaoEquipamentoRepository: SolicitacaoEquipamentoRepository,
  private val medicoRepository: MedicoRepository,
  private val salaRepository: SalaRepository,
  private val agendaAssistenteRepository: AgendaAssistenteRepository,
  private val agendaSalaRepository: AgendaSalaRepository,
  private val agendaSalaTipoEquipamentoRepository: AgendaSalaTipoEquipamentoRepository,
  private val historicoAgendamentoStatusService: HistoricoAgendamentoStatusService,
  private val agendamentoRepository: AgendamentoRepository,
  private val ativoRepository: AtivoRepository,
  private val tipoEquipamentoRepository: TipoEquipamentoRepository,
  private val userService: UserService,
  private val faturaService: FaturaService,
  private val custoOperacaoService: CustoOperacaoService,
  private val userRepository: UserRepository,
  private val historicoService : HistoricoAgendamentoStatusService,
  private val creditoRepository: CreditoRepository,
  private val faixaPrecoRepository: FaixaPrecoRepository,
  private val agendamentoService: AgendamentoService,
  private val auditLogService: AuditLogService,
  private val emailService: EmailService,
  private val descontoService: DescontoService
) {
  var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
  private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
  private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
  val MAX_TENTATIVAS = 5

  fun findById(id: Long): Optional<CarrinhoCompra> = carrinhoRepository.findById(id)

  @Transactional
  @Throws(AgendamentoVazioException::class)
  fun reservar(dto: AgendamentoRequestDto) : SolicitacaoAgendamentoDto  {
    var totalTentativasInvalidas: Int = 0
    val medico = medicoRepository.findById(dto.medicoId).get()
    val sala = salaRepository.findById(dto.sala).get()

    var currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

    if (dataInicioInvalida(dto))
      throw RuntimeException("Período inicial ${dto.dataInicio.toString()} ${dto.horaInicio.toString()} anterior à data atual: " + LocalDateTime.now())

    val carrinho = this.criarCarrinho(medico, currentUser)

    inicializarComValoresPadrao(dto)

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)


    var partsHoraInicio = dto.horaInicio!!.split(":")
    var dataInicio: LocalDateTime =  LocalDateTime.parse(dto.dataInicio + " 00:00", formatter)


    dataInicio = dataInicio.withHour(Integer.valueOf(partsHoraInicio[0])).withMinute(Integer.valueOf(partsHoraInicio[1]))
      .withSecond(Integer.valueOf(partsHoraInicio[2]))

    var partsHoraFim = dto.horaFim!!.split(":")

    var dataInicioTemp: LocalDateTime =  dataInicio.withHour(Integer.valueOf(partsHoraFim[0])).withMinute(Integer.valueOf(partsHoraFim[1]))
      .withSecond(Integer.valueOf(partsHoraFim[2]))


    var dataFim : LocalDateTime? = null
    if (dto.dataFim != null) {
      dataFim = LocalDateTime.parse(dto.dataFim + " 00:00", formatter)
      dataFim = dataFim.withHour(Integer.valueOf(partsHoraFim[0])).withMinute(Integer.valueOf(partsHoraFim[1]))
        .withSecond(Integer.valueOf(partsHoraFim[2]))
      dataInicioTemp = dataFim
    }

      val solicitacao = SolicitacaoReserva()
    solicitacao.refSalaEntity = sala
    solicitacao.status = SolicitacaoStatus.PENDENTE
    solicitacao.refCarrinhoCompraEntity = carrinho
    solicitacao.inicio = dataInicio
    solicitacao.fim =  dataFim ?: dataInicioTemp
    solicitacao.custo = 0.0
    solicitacao.refUserEntity = medico
    solicitacao.dateCreated = now
    solicitacao.version = 1

    // SALVAR SOLICITACAO
    solicitacaoReservaRepository.save(solicitacao)
    carrinho.solicitacoesReserva?.add(solicitacao)

    var agendamentos: MutableList<Agendamento> = arrayListOf()
    var errosValidacao: MutableList<ErroValidacaoReservaDto> = arrayListOf()

    val periodoAgendamento = PeriodoAgendamento.fromInt(dto.periodoAgendamento!!)

    var continuarReservando = dataFim != null && dataInicio <= dataFim || dto.maximoAgendamentos!! > 0


    val agendaSemanalAssistentes = agendaAssistenteRepository.findAllBySalaAndDiaDaSemanaInListAndHoraDoDiaBetween(dto.sala, dataInicio.hour, dataInicioTemp.hour, dto.diasSemana!!)

    val horarios = (dataInicio.hour) until dataInicioTemp.hour

    while (continuarReservando) {

      var diasSemana: MutableList<DayOfWeek> = arrayListOf()
      dto.diasSemana!!.forEach {
        diasSemana.add(DayOfWeek.of(it))
      }

      var diaSemanaInicio: DayOfWeek = dataInicio.dayOfWeek
      var index = diasSemana.indexOf(diaSemanaInicio)

      diasSemana = if ((index < 0 || diaSemanaInicio == DayOfWeek.SUNDAY)) diasSemana else diasSemana.subList(index, diasSemana.size)

      while (diasSemana.size > 0) {
        dataInicio = dataInicio.with(TemporalAdjusters.nextOrSame(diasSemana.removeAt(0)))

        if (agendamentos.size == dto.maximoAgendamentos || (dataFim != null && dataInicio > dataFim)) {
          break
        }

        var agendamento = Agendamento()
        agendamento.custo = 0.0
        agendamento.medico = medico
        agendamento.status = SolicitacaoStatus.PENDENTE
        agendamento.periodo = periodoAgendamento
        agendamento.agendaSalas = arrayListOf()
        agendamento.dateCreated = now
        agendamento.solicitacaoReserva = solicitacao
        agendamento.tipoReserva = TipoReserva.RESERVA

        var horaInicial = dataInicio.withHour(Integer.valueOf(partsHoraInicio[0])).withMinute(0)
          .withSecond(0).withNano(0)

        var horaFinal = dataInicio.withHour(Integer.valueOf(partsHoraFim[0])).withMinute(0)
          .withSecond(0).withNano(0)

        var agendaSalaAssistente: MutableList<AgendaSala> = mutableListOf()
        horarios.forEach { hora ->

          var filtroInicial = dataInicio.withHour(hora).withMinute(0).withSecond(0).withNano(0)

          var filtroFinal = dataInicio.withHour(hora + 1).withMinute(0).withSecond(0).withNano(0)

          var agendaSala = agendaSalaRepository.findBySalaAndInicioAndFim(dto.sala, filtroInicial, filtroFinal)

          var inicioTmp = dataInicio.withHour(hora ).withMinute(0).withSecond(0).withNano(0)

          var fimTmp = dataInicio.withHour(hora + 1).withMinute(0).withSecond(0).withNano(0)

          if (agendaSala == null &&
            agendaSemanalAssistentes.count {
              it.diaDaSemana == dataInicio.dayOfWeek.value
                && it.horaDoDia == hora && it.sala!!.id == sala!!.id
            } > 0) {

            agendaSala = AgendaSala()
            agendaSala.sala = sala
            agendaSala.fixo = false
            agendaSala.inicio = inicioTmp
            agendaSala.fim = fimTmp
            agendaSala.custo = 0.0
            agendaSala.assistente = agendaSemanalAssistentes.find { it.diaDaSemana == dataInicio.dayOfWeek.value && it.horaDoDia == hora && it.sala!!.id == sala!!.id }!!.assistente
          }

          if (agendaSala != null) {
            agendaSalaAssistente.add(agendaSala)
          } else {
            //AGENDAR COM ASSISTENTE VITUAL
            if (sala.assistenteVirtual != null) {
              //Criar nova agendaSala para Assistente Virtual
              var novaAgendasSala = AgendaSala()
              novaAgendasSala.custo = 0.0
              novaAgendasSala.sala = sala
              novaAgendasSala.inicio = inicioTmp
              novaAgendasSala.fim = fimTmp
              novaAgendasSala.fixo = false
              novaAgendasSala.assistente = sala.assistenteVirtual

              agendaSalaAssistente.add(novaAgendasSala)
            } else {
              var erro = ErroValidacaoReservaDto()
              erro.inicioPeriodo = dataInicio
              erro.fimPeriodo = dataInicio.withHour(dataInicio.hour + 1)
              erro.erro = "Não há Assistente Virtual registrado para esta sala"
              errosValidacao.add(erro)
            }

            /* ERRO ANTIGO - NÃO TEM ASSISTENTE DISPONÍVEL PARA ESSE PERÍODO
            var erro = ErroValidacaoReservaDto()
            erro.inicioPeriodo = dataInicio
            erro.fimPeriodo = dataInicio.withHour(dataInicio.hour + 1)
            erro.erro = "Não há assistente disponível para a sala ${sala?.nome} no dia ${dataInicio?.format(dateFormatter)}  entre ${hora}:00 e ${(hora + 1)}:00"
            errosValidacao.add(erro)
            */
          }
        }

        var agendaEmOutrasSalas = agendaSalaRepository.findAgendaEmOutrasSalasNoPeriodo(sala.id, horaInicial, horaFinal, medico.id)

        var (agendaDisponivel1, agendaEmManutencao) = agendaSalaAssistente.partition { it.manutencao == null }
        var (agendaDisponivel2, agendaComAgendamento) = agendaDisponivel1.partition { it.agendamento == null || it.agendamento!!.status == SolicitacaoStatus.CANCELADO }

        var agendaDisponivel: MutableList<AgendaSala> = mutableListOf()
        var agendadoEmOutrasSalas: MutableList<AgendaSala> = mutableListOf()

        agendaDisponivel2.forEach {
          var agenda = agendaEmOutrasSalas?.find { agendaEmOutraSala -> agendaEmOutraSala.inicio!!.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() == it.inicio!!.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() }

          if (agenda != null) {
            agendaEmOutrasSalas?.remove(agenda)
            agendadoEmOutrasSalas.add(agenda)
          } else {
            agendaDisponivel.add(it)
          }
        }

        agendaEmManutencao.groupBy { it.manutencao }.forEach { manutencao, agenda ->
          var erro = ErroValidacaoReservaDto()
          erro.inicioPeriodo = agenda.first().inicio!!
          erro.fimPeriodo = agenda.last().fim!!
          erro.erro = "A sala ${sala.nome} estará em manutenção no dia ${agenda.first().inicio?.format(dateFormatter)} entre ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)}"
          errosValidacao.add(erro)
        }

        agendaComAgendamento.groupBy { it.agendamento }.forEach { ag, agenda ->
          var erro = ErroValidacaoReservaDto()
          erro.inicioPeriodo = agenda.first().inicio!!
          erro.fimPeriodo = agenda.last().fim!!

          if (ag!!.tipoReserva == TipoReserva.MANUTENCAO)
            erro.erro = "Há um agendamento de manutenção na sala ${agenda.first().sala?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre às ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)} horas"
          else
            erro.erro = if (ag!!.medico!!.id == medico.id)
            "Já há uma sala ${agenda.first().sala?.nome} agendada para ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre às ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)} horas" else
            "A sala ${agenda.first().sala?.nome} está agendada para o médico ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)}"
          errosValidacao.add(erro)
        }

        agendadoEmOutrasSalas.groupBy { it.agendamento }.forEach { ag, agenda ->
          var erro = ErroValidacaoReservaDto()
          erro.inicioPeriodo = agenda.first().inicio!!
          erro.fimPeriodo = agenda.last().fim!!
          erro.erro = "Já há uma sala (${agenda.first().sala?.nome}) agendada para ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre às ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)} horas"

          errosValidacao.add(erro)
        }

        agendaDisponivel.forEach {
          it.agendamento = agendamento
          agendamento.agendaSalas!!.add(it)
        }

        if (!agendaDisponivel.isEmpty()) {
          agendamento.dia = agendaSalaAssistente.first().inicio!!.toLocalDate()
          agendamento.solicitacaoReserva = solicitacao
          solicitacao.refAgendamentoEntities?.add(agendamento)
          agendamentoRepository.save(agendamento)

          agendamentos.add(agendamento)
          totalTentativasInvalidas = 0
        } else {
          totalTentativasInvalidas++
        }
      }

      if (dto.repeticaoPeriodo == 0) {
        continuarReservando = false
      } else {
        dataInicio = proximaData(dto, dataInicio)
        continuarReservando = agendamentos.size < dto.maximoAgendamentos!! &&  ((dataFim == null && totalTentativasInvalidas < MAX_TENTATIVAS) || dataInicio.toEpochSecond(ZoneOffset.UTC) <= dataInicioTemp.toEpochSecond(ZoneOffset.UTC))
      }
    }

    // salva agendamentos
    agendamentoRepository.saveAll(agendamentos)
    val descontoTotal = custoOperacaoService.calcular(carrinho)

    carrinhoRepository.save(carrinho)

    var solicitacaoAgendamento = SolicitacaoAgendamentoDto()
    solicitacaoAgendamento.id = solicitacao.id
    solicitacaoAgendamento.sala = solicitacao?.refSalaEntity?.id
    solicitacaoAgendamento.carrinhoCompra = solicitacao?.refCarrinhoCompraEntity?.id
    solicitacaoAgendamento.inicio =  solicitacao.inicio
    solicitacaoAgendamento.fim = solicitacao.fim
    solicitacaoAgendamento.status = solicitacao.status
    solicitacaoAgendamento.custo =  solicitacao.custo
    solicitacaoAgendamento.descontoTotal = descontoTotal

    solicitacaoAgendamento.erros!!.addAll(errosValidacao)

    agendamentos.forEach { agendamento ->
      var slots = agendamento.agendaSalas!!.sortBy { it.inicio }
      var agendamentoDTO = agendamento.toDTO()
      //agendamentoDTO.agendamento = agendamento

      agendamentoDTO.sala = agendamento.agendaSalas!!.first().sala
      agendamentoDTO.assistente = agendamento.agendaSalas!!.first().assistente?.toCustomDTO()
      agendamentoDTO.inicio = agendamento.agendaSalas!!.minBy { it.inicio!! }!!.inicio!!
      agendamentoDTO.fim = agendamento.agendaSalas!!.maxBy { it.fim!! }!!.fim!!

      agendamento.agendaSalas!!.forEach{ ag ->
        val tiposEqui = ag?.agendaSalaTipoEquipamentos?.filter { it.status != SolicitacaoStatus.CANCELADO }

        if (tiposEqui != null)
          agendamentoDTO?.tiposEquipamentos?.addAll(tiposEqui)
      }
      agendamentoDTO.tipoReserva = TipoReserva.RESERVA

      solicitacaoAgendamento.agendamentoDTOs!!.add(agendamentoDTO)
    }

    if  (solicitacaoAgendamento.agendamentoDTOs.isNullOrEmpty())
      throw AgendamentoVazioException(solicitacaoAgendamento)

    return solicitacaoAgendamento
  }


  @Transactional
  fun cancelarSolicitacaoAgendamento(dto: CancelarAgendamentoDto): Boolean {

    var solicitacaoReserva = solicitacaoReservaRepository.findById(dto.solicitacaoReserva).get()

    if (solicitacaoReserva.status != SolicitacaoStatus.PENDENTE) {
      throw RuntimeException("Não é possível cancelar solicitações confirmadas")
    }
    var carrinho = solicitacaoReserva.refCarrinhoCompraEntity

    var agendamentos = agendamentoRepository.findAllBySolicitacaoReserva(solicitacaoReserva.id!!)

    if (!agendamentos.isEmpty()) {
      var agendasSala = agendaSalaRepository.findByAgendamentoIn(agendamentos)
      var solicitacoesEquipamentos: MutableList<SolicitacaoEquipamento> = arrayListOf()

      var agendasSalaTipoEquipamentos = agendaSalaTipoEquipamentoRepository.findByStatusNotEqualsAndAgendaSalaIn(SolicitacaoStatus.CANCELADO, agendasSala)

      agendasSalaTipoEquipamentos.forEach {
        solicitacoesEquipamentos!!.add(it.solicitacaoEquipamento!!);     }

      agendaSalaTipoEquipamentoRepository.deleteAll(agendasSalaTipoEquipamentos)

      solicitacaoEquipamentoRepository.deleteAll(solicitacoesEquipamentos)

      agendasSala.filter { it.fixo == false }.forEach {
        agendaSalaRepository.delete(it)
      }

      agendamentoRepository.deleteAll(agendamentos)

      solicitacaoReservaRepository.delete(solicitacaoReserva)

      custoOperacaoService.calcular(carrinho!!)
    }

    return true
  }

  @Transactional
  fun adicionarEquipamentos(dto: AgendamentoEquipamentoRequestDto): SolicitacaoEquipamentoDto {

    var agendaSalaList: MutableList<AgendaSala> = arrayListOf()

    var errosValidacao: MutableList<ErroValidacaoEquipamentoDto> = arrayListOf()

    var currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

    val medico = medicoRepository.findById(dto.medico).get()

    val sala = salaRepository.findById(dto.sala).get()

    val carrinhoCompra = this.criarCarrinho(medico, currentUser)


    dto.tiposEquipamentos?.forEach {

      var tipoEquipamento = tipoEquipamentoRepository.findById(it.tipoEquipamento).get()

      var solicitacaoEquipamento = criarSolicitacaoEquipamento(medico, currentUser, it, sala, carrinhoCompra, tipoEquipamento)
      solicitacaoEquipamentoRepository.save(solicitacaoEquipamento)
      carrinhoCompra.solicitacoesEquipamento?.add(solicitacaoEquipamento)

      val dataInicio = LocalDateTime.parse(it.inicio, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      val dataFim = LocalDateTime.parse(it.fim, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

      var agendasSalas = agendaSalaRepository.findByInicioAndFimOrderByInicioAsc(dataInicio, dataFim)
      var totalAtivosPorTipoEquipamento = ativoRepository.countByTipoEquipamento(tipoEquipamento)
      var totalUsado = 0

      if (agendasSalas != null){
        val listHistorico = historicoAgendamentoStatusService.findByIdAgendamento(agendasSalas.first().agendamento?.id)
        if (listHistorico.isNotEmpty())
          return throw RuntimeException("Não é possível adicionar um equipamento em um agendamento já iniciado")
      }

      agendasSalas.forEach { ag ->
        totalUsado += ag.agendaSalaTipoEquipamentos?.filter { it.status != SolicitacaoStatus.CANCELADO }?.count { it.tipoEquipamento?.id == tipoEquipamento.id }
          ?: 0
      }

      if (totalUsado < totalAtivosPorTipoEquipamento) {
        var ag = agendasSalas.find { it.sala?.id == sala.id }
        var agendaSalaTipoEquipamento = AgendaSalaTipoEquipamento()
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        agendaSalaTipoEquipamento.agendaSala = ag
        agendaSalaTipoEquipamento.dateCreated = LocalDateTime.now(saoPauloZone)
        agendaSalaTipoEquipamento.solicitacaoEquipamento = solicitacaoEquipamento
        agendaSalaTipoEquipamento.tipoEquipamento = tipoEquipamento
        agendaSalaTipoEquipamento.status = SolicitacaoStatus.PENDENTE
        agendaSalaTipoEquipamento.valor = tipoEquipamento.valorCobravel //ativoRepository.findByTipoEquipamentoOrderById(tipoEquipamento)?.first()?.valorCobravel
        agendaSalaTipoEquipamento.uuid = LocalDateTime.now().toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli()
        agendaSalaTipoEquipamento.agendaSala = ag
        ag?.agendaSalaTipoEquipamentos?.add(agendaSalaTipoEquipamento)
        agendaSalaList.add(ag!!)
        solicitacaoEquipamentoRepository.save(solicitacaoEquipamento)

      } else {
        var erroValidacao = ErroValidacaoEquipamentoDto()
        erroValidacao.erro = "Não há ${tipoEquipamento.nome} disponível no dia ${dataInicio.format(dateFormatter)} entre ${dataInicio.format(hourFormatter)} e ${dataFim.format(hourFormatter)}"
        errosValidacao.add(erroValidacao)
      }
    }

    var solicitacaoEquipamentoDto = SolicitacaoEquipamentoDto()
    solicitacaoEquipamentoDto.carrinho = carrinhoCompra.id
    if (!errosValidacao.isEmpty()) {
      solicitacaoEquipamentoDto.erros?.addAll(errosValidacao)
    }

    if (!agendaSalaList.isEmpty()) {
      agendaSalaRepository.saveAll(agendaSalaList)
      custoOperacaoService.calcular(carrinhoCompra)
      solicitacaoEquipamentoDto.agenda?.addAll(agendaSalaList)

    }
    return solicitacaoEquipamentoDto
  }


  private fun proximaData(dto: AgendamentoRequestDto, dataInicio: LocalDateTime): LocalDateTime {

    when (PeriodoReserva.valueOf(dto.periodo!!)) {
      PeriodoReserva.SEMANAL -> return dataInicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusWeeks(1 * dto.repeticaoPeriodo!!.toLong())
      PeriodoReserva.QUINZENAL -> return dataInicio.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).plusDays(14 * dto.repeticaoPeriodo!!.toLong())
      PeriodoReserva.MENSAL -> return dataInicio.withDayOfMonth(dataInicio.dayOfMonth).plusMonths(dto.repeticaoPeriodo!!.toLong())
    }

  }


  private fun criarCarrinho(medico: Medico, user: User): CarrinhoCompra {
    val saoPauloZone = java.time.ZoneId.of("America/Buenos_Aires")
    val now = LocalDateTime.now(saoPauloZone)

    var carrinhoCompra = getCarrinhoCompra(medico)
    if (carrinhoCompra != null && carrinhoCompra.status == SolicitacaoStatus.PENDENTE) {
      carrinhoCompra.lastUpdated = now
      return carrinhoCompra
    } else {
      carrinhoCompra = CarrinhoCompra()
      carrinhoCompra.medico = medico
      carrinhoCompra.custo = 0.0
      carrinhoCompra.desconto = 0.0
      carrinhoCompra.valor = 0.0
      carrinhoCompra.createUser = user
      carrinhoCompra.dateCreated = now
      carrinhoCompra.version = 1
      carrinhoCompra.status = SolicitacaoStatus.PENDENTE
      return carrinhoRepository.save(carrinhoCompra)
    }
  }

  private fun getCarrinhoCompra(currentUser: Medico): CarrinhoCompra? {
    return carrinhoRepository.findByStatusAndMedico(SolicitacaoStatus.PENDENTE, currentUser)

  }

  private fun dataInicioInvalida(dto: AgendamentoRequestDto): Boolean {
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)
    //.plusHours(1).withMinute(0).withSecond(0).withNano(0)
    val parts = dto.horaInicio!!.split(":")
    val inicio = LocalDateTime.parse(dto.dataInicio + " 00:00", formatter)

    return (inicio.withHour(Integer.valueOf(parts[0])).withMinute(Integer.valueOf(parts[1]))
      .withSecond(Integer.valueOf(parts[2])) < now)

  }

  private fun inicializarComValoresPadrao(dto: AgendamentoRequestDto) {
    dto.repeticaoPeriodo = dto.repeticaoPeriodo ?: 0

    if (dto.dataFim == null && dto.maximoAgendamentos == null) {
      dto.dataFim = dto.dataInicio
    }

    dto.maximoAgendamentos = dto.maximoAgendamentos ?: Int.MAX_VALUE

  }

  fun existsById(id: Long) = carrinhoRepository.existsById(id)


  private fun criarSolicitacaoEquipamento(medico: Medico, user: User, pTipoEquipamento: TipoEquipamentoRequestDto, sala: Sala, carrinhoCompra: CarrinhoCompra, tipoEquipamento : TipoEquipamento)
    : SolicitacaoEquipamento {

    val dataInicio = LocalDateTime.parse(pTipoEquipamento.inicio, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val dataFim = LocalDateTime.parse(pTipoEquipamento.fim, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    var solicitacaoEquipamento = SolicitacaoEquipamento()
    solicitacaoEquipamento.refSalaEntity = sala
    solicitacaoEquipamento.refUserEntity = medico
    solicitacaoEquipamento.inicio = dataInicio
    solicitacaoEquipamento.fim = dataFim
    solicitacaoEquipamento.status = SolicitacaoStatus.PENDENTE
    solicitacaoEquipamento.custo = 0.0
    solicitacaoEquipamento.refCarrinhoCompraEntity = carrinhoCompra
    solicitacaoEquipamento.tipoEquipamento = tipoEquipamento
    solicitacaoEquipamento.dateCreated = LocalDateTime.now(saoPauloZone)

    return solicitacaoEquipamento
  }


    @Transactional
    fun cancelarSolicitacaoEquipamento(dto: CancelarEquipamentoDto) : ResumoCancelamentoDto {
    var currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
      val resumoCancelamentoDTO = ResumoCancelamentoDto()

      val agendamento = agendamentoRepository.findById(dto.agendamento).get()

    val carrinho = criarCarrinho(agendamento.medico!!, currentUser)

    var agendaSalaTipoEquipamentoList: MutableList<AgendaSalaTipoEquipamento>? = arrayListOf()

    var tipoEquipamentoList = tipoEquipamentoRepository.findAllById(dto.tiposEquipamentos);

    if (agendamento.status == SolicitacaoStatus.CONFIRMADO) {
      val statusFiltro = listOf(SolicitacaoStatus.CONFIRMADO, SolicitacaoStatus.PENDENTE)
      agendaSalaTipoEquipamentoList = agendaSalaTipoEquipamentoRepository.findByStatusAgendaSalaTipoEquipamento(tipoEquipamentoList, statusFiltro, agendamento.agendaSalas)

    } else if (agendamento.status == SolicitacaoStatus.PENDENTE) {
      val statusFiltro = listOf(SolicitacaoStatus.PENDENTE)
      agendaSalaTipoEquipamentoList = agendaSalaTipoEquipamentoRepository.findByStatusAgendaSalaTipoEquipamento(tipoEquipamentoList, statusFiltro, agendamento.agendaSalas)
    }

    if (!agendaSalaTipoEquipamentoList?.isEmpty()!!) {
      val resumo = faturaService.gerarCreditoOuCancelamentoList(agendaSalaTipoEquipamentoList, agendamento)
      val resumo2 = faturaService.aplicarCreditosExistentes(agendamento.medico!!)

      agendaSalaTipoEquipamentoList.forEach {
        if (it.status == SolicitacaoStatus.PENDENTE) {
          it.solicitacaoEquipamento?.status = SolicitacaoStatus.CANCELADO
        }
        it.status = SolicitacaoStatus.CANCELADO
      }

      agendaSalaTipoEquipamentoList.groupBy { it.tipoEquipamento }.forEach { tipoEq, listTipoEq ->

        val tipoEquipamento = tipoEq?.nome
        val sala = listTipoEq.first().agendaSala?.sala?.nome
        val profissional = agendamento?.medico?.user?.nome

        val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        val dia = formatterData.format(listTipoEq.first().agendaSala?.inicio)

        val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
        val inicio = formatterHora.format(listTipoEq.first().agendaSala?.inicio)
        val fim = formatterHora.format(listTipoEq.last().agendaSala?.fim)

        val descricaoLog = "Equipamento '"+ tipoEquipamento +"' cancelado para o agendamento do profissional "+
                profissional +", na sala '"+ sala +"' no dia " + dia +" das " + inicio + " às " + fim
        auditLogService.criarLog(currentUser, LogEnum.REMOCAO_EQUIPAMENTO, descricaoLog)
      }


      resumoCancelamentoDTO.creditoRecebido = resumo.creditoGanho
      resumoCancelamentoDTO.creditoUtilizado = resumo2?.creditoUtilizado
      resumoCancelamentoDTO.fatura = resumo.fatura?.toDTO()

      agendaSalaTipoEquipamentoRepository.saveAll(agendaSalaTipoEquipamentoList)
      custoOperacaoService.calcular(carrinho)
      carrinhoRepository.save(carrinho)
    }
     return resumoCancelamentoDTO
    }

  fun getCurrentUserEmail(): String? = SecurityContextHolder.getContext().authentication.principal as String

  @Transactional
  fun finalizarCarrinho(creditCard: CartaoCreditoDto, user: User, medico: Medico, carrinho: CarrinhoCompra) {

    val solicitacoes = carrinho.solicitacoesReserva?.filter { it.status != SolicitacaoStatus.CANCELADO }

    if (solicitacoes?.isNotEmpty()!!)
      calcularCredito(solicitacoes, medico)

    carrinho.desconto = custoOperacaoService.calcularDesconto(carrinho)
    carrinho.valor = carrinho.custo!! - carrinho.desconto!!

    faturaService.gerar(carrinho!!, creditCard)

    val solicitacaoReservas =  solicitacaoReservaRepository.findByRefCarrinhoCompraEntityAndStatus(carrinho,SolicitacaoStatus.PENDENTE)
    val solicitacaoEquipamentos = solicitacaoEquipamentoRepository.findByRefCarrinhoCompraEntityAndStatus(carrinho,SolicitacaoStatus.PENDENTE)

    if (solicitacaoReservas.isNotEmpty()) {
      val agendamentos = agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE, solicitacaoReservas)
      if (agendamentos.isNotEmpty()) {
        agendamentos.forEach {
          it.status = SolicitacaoStatus.CONFIRMADO
          it.tipoReserva = TipoReserva.USO

          val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
          val dia = formatterData.format(it.dia)

          val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
          val inicio = formatterHora.format(it.agendaSalas?.first()?.inicio)
          val fim = formatterHora.format(it.agendaSalas?.last()?.fim)

          val descricaoLog = "Sala '" + it.agendaSalas?.first()?.sala?.nome + "' agendada para o dia " + dia + " das " + inicio + " às " + fim
          auditLogService.criarLog(user, LogEnum.AGENDAMENTO, descricaoLog)

          emailService.novoAgendamento(medico.user?.username, it)
        }
        agendamentoRepository.saveAll(agendamentos)
      }
      solicitacaoReservas.forEach{
        it.status = SolicitacaoStatus.CONFIRMADO
      }
      solicitacaoReservaRepository.saveAll(solicitacaoReservas)
    }

    if (!solicitacaoEquipamentos.isEmpty()) {
      val agendaSalaTipoEquipamentoList = agendaSalaTipoEquipamentoRepository.findByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos)
      if (!agendaSalaTipoEquipamentoList.isEmpty()) {
        agendaSalaTipoEquipamentoList.forEach {
          it.status = SolicitacaoStatus.CONFIRMADO
        }


        agendaSalaTipoEquipamentoList.groupBy { it.tipoEquipamento }.forEach { tipoEquipamento, agSalasTpEq ->

          agSalasTpEq.groupBy { it.agendaSala?.agendamento }.forEach { agendamento, agendaSalasTipoEs ->

            val tipoEquipamento = tipoEquipamento?.nome
            val sala = agendaSalasTipoEs.first().agendaSala?.sala?.nome
            val profissional = agendamento?.medico?.user?.nome

            val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            val dia = formatterData.format(agendaSalasTipoEs.first().agendaSala?.inicio)

            val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
            val inicio = formatterHora.format(agendaSalasTipoEs.first().agendaSala?.inicio)
            val fim = formatterHora.format(agendaSalasTipoEs.last().agendaSala?.fim)

            val descricaoLog = "Equipamento '"+ tipoEquipamento +"' adicionado para o agendamento do profissional "+
                    profissional +", na sala '"+ sala +"' no dia " + dia +" das " + inicio + " às " + fim
            auditLogService.criarLog(user, LogEnum.ADICAO_EQUIPAMENTO, descricaoLog)
          }
        }
        agendaSalaTipoEquipamentoRepository.saveAll(agendaSalaTipoEquipamentoList)
      }

      solicitacaoEquipamentos.forEach{
        it.status = SolicitacaoStatus.CONFIRMADO
      }
      
      solicitacaoEquipamentoRepository.saveAll(solicitacaoEquipamentos)
    }



    carrinho?.status = SolicitacaoStatus.CONFIRMADO

    carrinhoRepository.save(carrinho)
  }

  fun precosEquipamentosUtilizados(carrinhoCompra: CarrinhoCompra): Any? =
    agendaSalaTipoEquipamentoRepository.findByCarrinhoAndPendente(carrinhoCompra)

  fun cancelarPorIdProfissional(idProfissional: Long): Boolean {
    var medico: Medico

    try {
      medico = medicoRepository.findById(idProfissional).get()
    }catch (e: Exception){
      return false
    }

    if (medico == null)
      return false

    return cancelar(medico)
  }

  fun cancelarPorProfissionalLogado(): Boolean {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    var medico: Medico

    try {
      medico = medicoRepository.findByUser(currentUser).get()
    }catch (e: Exception){
      return false
    }

    if (medico == null)
      return false

    return cancelar(medico)
  }

  @Transactional
  fun cancelar(medico: Medico): Boolean {

    //val agendamento = agendamentoRepository.findById(dto.agendamento).get()
    var carrinho:CarrinhoCompra? = null
    try {
       carrinho = carrinhoRepository.findByStatusAndMedico(SolicitacaoStatus.PENDENTE, medico)!! //criarCarrinho(agendamento.medico!!, currentUser)
    }catch (e: Exception){
      return false
    }


    if (carrinho == null || carrinho.status != SolicitacaoStatus.PENDENTE)
      return false

    val solicitacaoAgendamentos = carrinho.solicitacoesReserva?.filter { it.refCarrinhoCompraEntity != null }
    val solicitacaoEquipamentos = carrinho.solicitacoesEquipamento?.filter { it.refCarrinhoCompraEntity != null }

    val agendamentos = if (!solicitacaoAgendamentos?.isEmpty()!!)  agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE,solicitacaoAgendamentos ) else mutableListOf()
    if (!agendamentos.isEmpty()) {
      agendamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
      agendamentoRepository.saveAll(agendamentos)
    }

    if (!solicitacaoAgendamentos.isNullOrEmpty()) {
      solicitacaoAgendamentos?.forEach { sol -> sol.status = SolicitacaoStatus.CANCELADO }
      solicitacaoReservaRepository.saveAll(solicitacaoAgendamentos)
    }

    val equipamentos = if (!solicitacaoEquipamentos.isNullOrEmpty())  agendaSalaTipoEquipamentoRepository.findAllByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos) else mutableListOf()
    if (!equipamentos?.isEmpty()) {
      equipamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
      agendaSalaTipoEquipamentoRepository.saveAll(equipamentos)
    }

    if (!solicitacaoEquipamentos.isNullOrEmpty()) {
      solicitacaoEquipamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
      solicitacaoEquipamentoRepository.saveAll(solicitacaoEquipamentos)
    }

    solicitacaoAgendamentos.filter{ it.status == SolicitacaoStatus.CANCELAMENTO_PENDENTE }.forEach {
      it.status == SolicitacaoStatus.CONFIRMADO
      solicitacaoReservaRepository.save(it)

    }

    agendamentoRepository.findByStatus(SolicitacaoStatus.CANCELAMENTO_PENDENTE).forEach {
      it.status == SolicitacaoStatus.CONFIRMADO
      agendamentoRepository.save(it)
    }

    carrinho.status = SolicitacaoStatus.CANCELADO

    carrinhoRepository.save(carrinho)
    return true
  }

  @Transactional(readOnly = true)
  fun resumoCompraPorMes(carrinhoCompra: CarrinhoCompra): Any? {
      val resumoCompraDto = ResumoCompraDto()
      resumoCompraDto.valor=carrinhoCompra.valor
      resumoCompraDto.desconto=carrinhoCompra.desconto
      resumoCompraDto.resumosMensais= mutableListOf()
    val desconto = carrinhoCompra.desconto

    // Custo agendamento de salas por mês
    val solicitacaoReservas = solicitacaoReservaRepository.findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra, SolicitacaoStatus.PENDENTE)
    var agendamentos = mutableListOf<Agendamento>()

    if (solicitacaoReservas != null)
      agendamentos = agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE, solicitacaoReservas)

    agendamentos.groupBy { it.dia?.format(DateTimeFormatter.ofPattern("MM/yyyy")) }.forEach { mesAno, agendamentosMes ->
      val primeiroDiaMes = LocalDate.of(mesAno?.split('/')?.get(1)?.toInt()!!, mesAno?.split('/')?.get(0)?.toInt(), 1)

      var dto = ResumoMensalDto()
      dto.mesAno=primeiroDiaMes
      dto.custo=agendamentosMes.sumByDouble{ it.custo!! }
      dto.agendamentos=agendamentosMes.toMutableList()

      resumoCompraDto.resumosMensais?.add(dto)
    }

    // Custo de equipamentos agendados por mês
    val solicitacaoEquipamentos = solicitacaoEquipamentoRepository.findByRefCarrinhoCompraEntityAndStatus(carrinhoCompra, SolicitacaoStatus.PENDENTE)
    var equipamentos = mutableListOf<AgendaSalaTipoEquipamento>()
      if (solicitacaoEquipamentos != null)
        equipamentos = agendaSalaTipoEquipamentoRepository.findAllByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos)

    equipamentos.groupBy { it.agendaSala?.inicio?.format(DateTimeFormatter.ofPattern("MM/yyyy")) }.forEach { mesAno, equipamentosMes ->
      val primeiroDiaMes = LocalDate.of(mesAno?.split('/')?.get(1)?.toInt()!!, mesAno?.split('/')?.get(0)?.toInt(), 1)
      var resumoMensalDto = resumoCompraDto.resumosMensais?.find { it.mesAno?.isEqual(primeiroDiaMes)!! }

      if (resumoMensalDto == null) {
        val resumoMensalDto = ResumoMensalDto()
        resumoMensalDto.mesAno=primeiroDiaMes
        resumoMensalDto.custo=equipamentosMes.sumByDouble{ it.valor!! }
        resumoMensalDto.equipamentos=equipamentosMes.toMutableList()

        resumoCompraDto.resumosMensais?.add(resumoMensalDto)
      } else {
        resumoMensalDto.custo?.plus(equipamentosMes.sumByDouble{ it.valor!! }) ?: 0
        resumoMensalDto.equipamentos = equipamentosMes.toMutableList()
      }
    }
    return resumoCompraDto
  }

  @Transactional(readOnly = true)
  fun detalharPorMesESalaEHorario(carrinhoCompra: CarrinhoCompra): Any? {
    var resumoCompra = mutableMapOf<Month, MutableList<ResumoCompraDetalhadoDto>>()

    var agendasSalas = agendaSalaRepository.findAllBySolicitacaoReservaAndCarrinhoAndPendente(carrinhoCompra.id!!)

    agendasSalas.groupBy { it.sala }.forEach { sala, agendasPorSala ->
      agendasPorSala.forEach {
        val custoEquipamento = it.agendaSalaTipoEquipamentos?.filter { agte -> agte.status != SolicitacaoStatus.CANCELADO }?.sumByDouble { agte -> agte.valor!! } ?: 0.0
        val resumoCompraDetalhadoDTO = ResumoCompraDetalhadoDto(
            sala = sala,
            data = it.inicio?.toLocalDate(),
            horaInicio = it.inicio?.toLocalTime(),
            horaFim = it.fim?.toLocalTime(),
            custo = it.custo,
            custoEquipamentos = custoEquipamento,
            total = it.custo?.plus(custoEquipamento)
        )

        if (resumoCompra[resumoCompraDetalhadoDTO.data?.month].isNullOrEmpty()) {
          var list = mutableListOf(resumoCompraDetalhadoDTO)
          resumoCompra.put(resumoCompraDetalhadoDTO.data?.month!!,list)
        } else
          resumoCompra[resumoCompraDetalhadoDTO.data?.month!!]?.add(resumoCompraDetalhadoDTO)
      }
    }

    var agendasSalas2 = agendaSalaRepository.findAllBySolicitacaoEquipamentoAndCarrinhoAndPendente(carrinhoCompra!!.id)

    agendasSalas2.groupBy { it.sala }.forEach { sala, agenda ->
      agenda.forEach { ag ->
        var resumoAtualMes = resumoCompra.get(ag.inicio?.month!!)
        var atual = resumoAtualMes?.find { it.sala == sala && it.data?.isEqual(ag.inicio?.toLocalDate())!! && it.horaInicio?.equals(ag.inicio?.toLocalTime())!! && it.horaFim?.equals(ag.fim?.toLocalTime())!! }

        var custoEquipamento = ag.agendaSalaTipoEquipamentos?.filter { agte -> agte.status == SolicitacaoStatus.PENDENTE }?.sumByDouble { agte -> agte.valor!! } ?: 0.0
        if (atual == null) {
          var resumoCompraDetalhadoDTO = ResumoCompraDetalhadoDto(
            sala = sala,
            data = ag.inicio?.toLocalDate(),
            horaInicio = ag.inicio?.toLocalTime(),
            horaFim = ag.fim?.toLocalTime(),
            custo = 0.0,
            custoEquipamentos = custoEquipamento,
            total = custoEquipamento
          )

          if (resumoAtualMes == null) {
            var list = mutableListOf(resumoCompraDetalhadoDTO)
            resumoCompra.put(resumoCompraDetalhadoDTO.data?.month!!,list)

          } else {
            resumoAtualMes?.add(resumoCompraDetalhadoDTO)
          }
        } else {
          atual!!.custoEquipamentos = custoEquipamento
        }
      }
    }
    var retorno = mutableListOf<DetalhadoDto>()

    resumoCompra.forEach { month, resumos ->
      var dto = DetalhadoDto()
      dto.custo = resumos.sumByDouble {  it.custo!!  }.plus(resumos.sumByDouble {  it.custoEquipamentos!!  })
      dto.detalhes = resumos
      dto.mesReferencia = month.toString()

      retorno.add(dto)
    }

    return retorno
  }

  fun findByAgendamento (idAgendamento: Long) = carrinhoRepository.findByAgendamento(idAgendamento)


  @Transactional(readOnly = true)
  fun salvar(creditCard: CartaoCreditoDto) {
    var currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    var medico = medicoRepository.findByUser(currentUser).get()

    val carrinhoCompra = this.getCarrinhoCompra(medico)

    if (carrinhoCompra != null) {
      faturaService.gerar(carrinhoCompra, creditCard)
    }

    var  solicitacaoReservas = carrinhoCompra?.solicitacoesReserva?.filter { it.status == SolicitacaoStatus.PENDENTE }
    var solicitacaoEquipamentos = carrinhoCompra?.solicitacoesEquipamento?.filter { it.status == SolicitacaoStatus.PENDENTE }

    if (solicitacaoReservas != null) {
      var agendamentos: List<Agendamento>  = agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE, solicitacaoReservas)
      if (agendamentos.isNotEmpty()) {
        agendamentos.forEach {
          it.status = SolicitacaoStatus.CONFIRMADO
        }
        agendamentoRepository.saveAll(agendamentos)
      }
    }

    if (solicitacaoEquipamentos != null) {
      if (solicitacaoEquipamentos.isNotEmpty()) {
        var agendaSalaTipoEquipamentoList: List<AgendaSalaTipoEquipamento>  = agendaSalaTipoEquipamentoRepository.findAllByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos)
        if (agendaSalaTipoEquipamentoList.isNotEmpty()) {
          agendaSalaTipoEquipamentoList.forEach() {
            it.status = SolicitacaoStatus.CONFIRMADO
          }
          agendaSalaTipoEquipamentoRepository.saveAll(agendaSalaTipoEquipamentoList)
        }
      }
    }



    carrinhoCompra?.status = SolicitacaoStatus.CONFIRMADO
  }

  fun atualizarStatusCheckAgendamento(assistente: User, medico : User, agendamentoOrigem : Agendamento, agendamentoDestino : Agendamento) {

    //CHECK OUT
    var dtoOrigemAssistente = HistoricoAgendamentoStatusDto(null, null, null, null, null, null, null)
    dtoOrigemAssistente.agendamentoId = agendamentoOrigem
    dtoOrigemAssistente.userId = assistente
    dtoOrigemAssistente.dateCreated = LocalDateTime.now()
    dtoOrigemAssistente.status = AgendamentoStatus.OK

    var relOrigem = historicoAgendamentoStatusService.save(dtoOrigemAssistente).toDTO()

    var validaCheckOut = historicoAgendamentoStatusService.validaCheckout(agendamentoOrigem.id)

    var dtoOrigemMedico = HistoricoAgendamentoStatusDto(null, null, null, null, null, null, null)
    dtoOrigemMedico.agendamentoId = agendamentoOrigem
    dtoOrigemMedico.userId = medico
    dtoOrigemMedico.dateCreated = LocalDateTime.now()
    dtoOrigemMedico.status = AgendamentoStatus.OK

     relOrigem = historicoAgendamentoStatusService.save(dtoOrigemMedico).toDTO()

     validaCheckOut = historicoAgendamentoStatusService.validaCheckout(agendamentoOrigem.id)

    // CHECK IN
    var dtoDestinoAssistente = HistoricoAgendamentoStatusDto(null, null, null, null, null, null, null)
    dtoDestinoAssistente.agendamentoId = agendamentoDestino
    dtoDestinoAssistente.userId = assistente
    dtoDestinoAssistente.dateCreated = LocalDateTime.now()
    dtoDestinoAssistente.status = AgendamentoStatus.OK

    var relDestino = historicoAgendamentoStatusService.save(dtoDestinoAssistente).toDTO()

    var validaCheckIn = historicoAgendamentoStatusService.validaCheckin(agendamentoDestino.id)

    var dtoDestinoMedico = HistoricoAgendamentoStatusDto(null, null, null, null, null, null, null)
    dtoDestinoMedico.agendamentoId = agendamentoDestino
    dtoDestinoMedico.userId = medico
    dtoDestinoMedico.dateCreated = LocalDateTime.now()
    dtoDestinoMedico.status = AgendamentoStatus.OK

    relDestino = historicoAgendamentoStatusService.save(dtoDestinoMedico).toDTO()

    validaCheckIn = historicoAgendamentoStatusService.validaCheckout(agendamentoDestino.id)

  }



  @Transactional
  @Throws(ErroRealocarAgendamentoException::class)
  fun alterarAgendamento(dto: AlterarAgendamentoDto): SolicitacaoAgendamentoDto {

    val agendamento = agendamentoRepository.findById(dto.agendamento).get()

    if (agendamento.alterou != null && agendamento.alterou!!) {
      throw RuntimeException("Já foi feita uma alteração de horário no agendamento. Operação não realizada !")
    }

    if (!agendamento.agendamentoStatusAtual?.equals(AgendamentoStatus.OK)!!) {
      throw RuntimeException("Não é permitido alteração de horário em agendamento não iniciado. Operação não realizada !")
    }


    if (agendamento.agendamentoPai != null ) {
      throw RuntimeException("Não é possivel alterar horário de uma extensão de agendamento. Operação não realizada !")
    }


    val sala = agendamento.agendaSalas?.get(0)?.sala

    val inicio = agendamento.agendaSalas?.first()?.inicio

    val fim = agendamento.agendaSalas?.last()?.fim

    val medico = agendamento.medico

    var currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

    val carrinho = this.criarCarrinho(medico!!, currentUser)

    var diaSemana: DayOfWeek? = inicio?.dayOfWeek

    val diasSemana = mutableListOf<Int>()
    diasSemana.add(diaSemana!!.value)

    val agendaSemanalAssistentes = agendaAssistenteRepository.findAllBySalaAndDiaDaSemanaInListAndHoraDoDiaBetween(sala?.id, inicio?.hour, fim!!.hour, diasSemana)

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)

    val horaInicial = agendamento.agendaSalas?.last()?.fim
    val horaFinal = horaInicial!!.plusHours(dto.horas!!)

    var finalDia = LocalDateTime.now().withHour(22).withMinute(0).withSecond(0)


    if (horaFinal.isAfter(finalDia) ) {
      throw RuntimeException("Hora final do agendamento excede as 22 horas ")
    }

    var agendamentos: MutableList<Agendamento> = arrayListOf()
    var errosValidacao: MutableList<ErroValidacaoReservaDto> = arrayListOf()

    val horarios = (horaInicial?.hour) until horaFinal.hour

    val solicitacao = SolicitacaoReserva()

    solicitacao.refSalaEntity = sala
    solicitacao.status = SolicitacaoStatus.PENDENTE
    solicitacao.refCarrinhoCompraEntity = carrinho
    solicitacao.inicio = horaInicial
    solicitacao.fim =  horaFinal
    solicitacao.custo = 0.0
    solicitacao.refUserEntity = medico
    solicitacao.dateCreated = now
    solicitacao.version = 1

    // SALVAR SOLICITACAO
    solicitacaoReservaRepository.save(solicitacao)
    carrinho.solicitacoesReserva?.add(solicitacao)

    var solicitacaoAgendamento = SolicitacaoAgendamentoDto()

    var agendamentoNovo = Agendamento()
    agendamentoNovo.custo = 0.0
    agendamentoNovo.medico = medico
    agendamentoNovo.status = SolicitacaoStatus.PENDENTE
    agendamentoNovo.periodo = PeriodoAgendamento.fromInt(1)
    agendamentoNovo.agendaSalas = arrayListOf()
    agendamentoNovo.dateCreated = now
    agendamentoNovo.solicitacaoReserva = solicitacao
    agendamentoNovo.agendamentoPai = agendamento

    var agendaSalaAdicionais: MutableList<AgendaSala> = mutableListOf()

    var agendaSalaAssistente: MutableList<AgendaSala> = mutableListOf()
        horarios.forEach { hora ->

          var filtroInicial = inicio?.withHour(hora)?.withMinute(0)?.withSecond(0)?.withNano(0)

          var filtroFinal = inicio?.withHour(hora + 1)?.withMinute(0)?.withSecond(0)?.withNano(0)

          var inicioTmp = inicio?.withHour(hora )?.withMinute(0)?.withSecond(0)?.withNano(0)

          var fimTmp = inicio?.withHour(hora + 1)?.withMinute(0)?.withSecond(0)?.withNano(0)

          var agendaSala = AgendaSala()
          agendaSala.sala = sala
          agendaSala.fixo = false
          agendaSala.inicio = inicioTmp
          agendaSala.fim = fimTmp
          agendaSala.custo = 0.0
          agendaSala.assistente =  agendamento?.agendaSalas!!.first().assistente  //agendaSemanalAssistentes.find { it.diaDaSemana == inicio?.dayOfWeek?.value && it.horaDoDia == hora && it.sala!!.id == sala!!.id }!!.assistente

          agendaSalaAdicionais.add(agendaSala)


          var agendaSalaCorrente = agendaSalaRepository.findBySalaAndInicioAndFim(sala?.id, filtroInicial, filtroFinal)


          if (agendaSalaCorrente != null ) {
            agendaSalaAssistente.add(agendaSalaCorrente)
          }
        }

      var agendaEmOutrasSalas = agendaSalaRepository.findAgendaEmOutrasSalasNoPeriodo(sala?.id, horaInicial, horaFinal, medico?.id)

      var (agendaDisponivel1, agendaEmManutencao) = agendaSalaAssistente.partition { it.manutencao == null }
      var (agendaDisponivel2, agendaComAgendamento) = agendaDisponivel1.partition { it.agendamento == null || it.agendamento!!.status == SolicitacaoStatus.CANCELADO }

      var agendaDisponivel: MutableList<AgendaSala> = mutableListOf()
      var agendadoEmOutrasSalas: MutableList<AgendaSala> = mutableListOf()

      var agendamentoRealocar: MutableList<Agendamento> = mutableListOf()


      var gerarAgendamento = true

      agendaDisponivel2.forEach {
          var agenda = agendaEmOutrasSalas?.find { agendaEmOutraSala -> agendaEmOutraSala.inicio!!.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() == it.inicio!!.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() }

          if (agenda != null) {
            agendaEmOutrasSalas?.remove(agenda)
            agendadoEmOutrasSalas.add(agenda)
          } else {
            agendaDisponivel.add(it)
          }
        }


        agendaEmManutencao.groupBy { it.manutencao }.forEach { manutencao, agenda ->
          var erro = ErroValidacaoReservaDto()
          erro.inicioPeriodo = agenda.first().inicio!!
          erro.fimPeriodo = agenda.last().fim!!
          erro.erro = "A sala ${sala?.nome} estará em manutenção no dia ${agenda.first().inicio?.format(dateFormatter)} entre ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)}"
          errosValidacao.add(erro)
          gerarAgendamento = false
        }

        agendadoEmOutrasSalas.groupBy { it.agendamento }.forEach { ag, agenda ->
          var erro = ErroValidacaoReservaDto()
          erro.inicioPeriodo = agenda.first().inicio!!
          erro.fimPeriodo = agenda.last().fim!!
          erro.erro = "Já há uma sala (${agenda.first().sala?.nome}) agendada para ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre às ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)} horas"
          errosValidacao.add(erro)
          gerarAgendamento = false
        }

        if (gerarAgendamento) {
          agendaComAgendamento.groupBy { it.agendamento }.forEach { ag, agenda ->
            var erro = ErroValidacaoReservaDto()
            erro.inicioPeriodo = agenda.first().inicio!!
            erro.fimPeriodo = agenda.last().fim!!
            erro.erro = if (ag!!.medico!!.id == medico?.id)
              "Já há uma sala ${agenda.first().sala?.nome} agendada para ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre às ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)} horas" else
              "A sala ${agenda.first().sala?.nome} está agendada para o médico ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)}"

            if (ag!!.medico!!.id != medico?.id) {
              agendamentoRealocar.add(ag)
            } else {
              errosValidacao.add(erro)
            }
          }

          for (agendaRealocar: Agendamento in agendamentoRealocar) {

            var inicioRealocar = agendaRealocar.agendaSalas?.first()?.inicio

            var fimRealocar = agendaRealocar.agendaSalas?.last()?.fim

            var salaRealocar = agendaRealocar.agendaSalas?.get(0)?.sala

            var salasDisponiveis = salaRepository.findAllDisponivelAndReservaAndEspecialidade(salaRealocar?.refEspecialidadeEntity?.id)

            if (salasDisponiveis.isNullOrEmpty()) {
              throw RuntimeException("Não existem salas disponíveis para realocar o agendamento")
            }

            for (salaDisp: Sala in salasDisponiveis) {
             // var agendaAssistentes = agendaAssistenteRepository.findAllBySalaAndDiaDaSemanaInListAndHoraDoDiaBetween(salaDisp.id, inicioRealocar?.hour, fimRealocar?.hour, diasSemana)
              var assistentePrincipal = agendaAssistenteRepository.findAssistentePrincipalBySala(salaDisp.id)
              var agendaSalasOcupada = agendaSalaRepository.findAllBySalaAndInicioAndFim(salaDisp.id, inicioRealocar, fimRealocar)


              if ( assistentePrincipal.isNullOrEmpty()) {
                var erro = ErroValidacaoReservaDto()
                erro.inicioPeriodo = inicioRealocar
                erro.fimPeriodo = fimRealocar
                erro.erro = "Não há assistente disponível para a sala reserva ${salaDisp?.nome} no dia ${inicioRealocar?.format(dateFormatter)}  entre ${inicioRealocar?.hour}:00 e ${(fimRealocar?.hour)}:00"
                errosValidacao.add(erro)

                throw ErroRealocarAgendamentoException(errosValidacao)

              }

/*
              if ( agendaAssistentes.count {
                  it.diaDaSemana == inicioRealocar?.dayOfWeek!!.value
                    && it.horaDoDia == inicioRealocar?.hour && it.sala!!.id == salaDisp!!.id
                }  == 0 ) {
                var erro = ErroValidacaoReservaDto()
                erro.inicioPeriodo = inicioRealocar
                erro.fimPeriodo = inicioRealocar?.withHour(inicioRealocar?.hour + 1)
                erro.erro = "Não há assistente disponível para a sala reserva ${salaDisp?.nome} no dia ${inicioRealocar?.format(dateFormatter)}  entre ${inicioRealocar?.hour}:00 e ${(fimRealocar?.hour)}:00"
                errosValidacao.add(erro)

                throw ErroRealocarAgendamentoException(errosValidacao)

              }
*/



              if (agendaSalasOcupada.isNullOrEmpty()) {
                agendaRealocar.solicitacaoReserva?.refSalaEntity = salaDisp
                agendaRealocar.lastUpdated = LocalDateTime.now()

                agendaRealocar.agendaSalas?.forEach { agLocal ->
                  agLocal.sala = salaDisp
                  agLocal.assistente = assistentePrincipal?.first().assistente // .find { it.diaDaSemana == inicioRealocar?.dayOfWeek?.value && it.horaDoDia == inicioRealocar?.hour && it.sala!!.id == salaDisp!!.id }!!.assistente

                }
                agendamentoRepository.save(agendaRealocar)
                agendamentos.add(agendaRealocar)

                break
              }

            }

          }

          if (agendamentoRealocar.size  != agendamentos.size ) {
            agendamentoRealocar.removeAll(agendamentos)
            agendamentoRealocar.forEach {agenda ->
              var erro = ErroValidacaoReservaDto()
              erro.inicioPeriodo = agenda.agendaSalas!!.first()?.inicio!!
              erro.fimPeriodo = agenda.agendaSalas!!.last()?.fim!!
              erro.erro = "Não foi possível realocar o agendamento da sala ${agenda.agendaSalas?.first()?.sala?.nome} para o médico ${agenda?.medico?.user?.nome} no dia ${agenda.agendaSalas?.first()?.inicio?.format(dateFormatter)} entre ${agenda.agendaSalas?.first()?.inicio?.format(hourFormatter)} e ${agenda.agendaSalas?.last()?.fim?.format(hourFormatter)}"
              errosValidacao.add(erro)
            }
            throw ErroRealocarAgendamentoException(errosValidacao)
          } else {

            agendamentoRealocar.forEach{agendaRealocar ->
              val descricaoLog = "Agendamento realocado para sala '${agendaRealocar.agendaSalas?.first()?.sala?.nome}'," +
                      " para o dia ${agendaRealocar.agendaSalas?.first()?.inicio?.format(dateFormatter)} entre ${agendaRealocar.agendaSalas?.first()?.inicio?.format(hourFormatter)} e ${agendaRealocar.agendaSalas?.last()?.fim?.format(hourFormatter)}"
              auditLogService.criarLog(agendaRealocar?.medico?.user!!, LogEnum.REALOCACAO_AGENDAMENTO, descricaoLog)
            }
          }

          if (agendaSalaAdicionais.isNotEmpty()) {
            agendaSalaAdicionais.forEach {
                  it.agendamento = agendamentoNovo
               agendamentoNovo?.agendaSalas!!.add(it)
             }

            agendamentoNovo.dia = agendaSalaAdicionais.first().inicio!!.toLocalDate()
            agendamentoNovo.solicitacaoReserva = solicitacao
            solicitacao.refAgendamentoEntities?.add(agendamentoNovo)
            agendamento.alterou = true
            agendamentoRepository.save(agendamento)

            val descricaoLog = "Aumento de ${dto.horas} horas para o agendamento da sala '${agendamento.agendaSalas?.first()?.sala?.nome}'," +
                    " que agora ocorrerá no dia ${agendamento.agendaSalas?.first()?.inicio?.format(dateFormatter)}" +
                    " entre ${agendamento.agendaSalas?.first()?.inicio?.format(hourFormatter)} e ${agendamentoNovo.agendaSalas?.last()?.fim?.format(hourFormatter)}"
            auditLogService.criarLog(agendamento.agendaSalas!!.first().assistente?.user!!, LogEnum.AUMENTO_PERIODO, descricaoLog)

            agendamentoRepository.save(agendamentoNovo)

            custoOperacaoService.calcular(carrinho)

            carrinhoRepository.save(carrinho)

            faturarAlteracao(carrinho)

            carrinhoRepository.save(carrinho)

            atualizarStatusCheckAgendamento(currentUser , medico.user!!, agendamento, agendamentoNovo)


            solicitacaoAgendamento.id = solicitacao.id
            solicitacaoAgendamento.sala = solicitacao?.refSalaEntity?.id
            solicitacaoAgendamento.carrinhoCompra = solicitacao?.refCarrinhoCompraEntity?.id
            solicitacaoAgendamento.inicio =  solicitacao.inicio
            solicitacaoAgendamento.fim = solicitacao.fim
            solicitacaoAgendamento.status = solicitacao.status
            solicitacaoAgendamento.custo =  solicitacao.custo

            solicitacaoAgendamento.erros!!.addAll(errosValidacao)

              var slots = agendamentoNovo.agendaSalas!!.sortBy { it.inicio }
              var agendamentoDTO = agendamentoNovo.toDTO()

              agendamentoDTO.sala = agendamentoNovo.agendaSalas!!.first().sala
              agendamentoDTO.assistente = agendamentoNovo.agendaSalas!!.first().assistente?.toCustomDTO()
              agendamentoDTO.inicio = agendamentoNovo.agendaSalas!!.minBy { it.inicio!! }!!.inicio!!
              agendamentoDTO.fim = agendamentoNovo.agendaSalas!!.maxBy { it.fim!! }!!.fim!!

              agendamento.agendaSalas!!.forEach{ ag ->
                val tiposEqui = ag?.agendaSalaTipoEquipamentos?.filter { it.status != SolicitacaoStatus.CANCELADO }

                if (tiposEqui != null)
                  agendamentoDTO?.tiposEquipamentos?.addAll(tiposEqui)
              }
              agendamentoDTO.tipoReserva = TipoReserva.RESERVA

              solicitacaoAgendamento.agendamentoDTOs!!.add(agendamentoDTO)


          }

        }
    return solicitacaoAgendamento
    }

  private fun faturarAlteracao(carrinho: CarrinhoCompra) {
    faturaService.gerar(carrinho!!)

    val solicitacaoReservas = solicitacaoReservaRepository.findByRefCarrinhoCompraEntityAndStatus(carrinho, SolicitacaoStatus.PENDENTE)
    val solicitacaoEquipamentos = solicitacaoEquipamentoRepository.findByRefCarrinhoCompraEntityAndStatus(carrinho, SolicitacaoStatus.PENDENTE)

    if (solicitacaoReservas.isNotEmpty()) {
      val agendamentos = agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE, solicitacaoReservas)
      if (agendamentos.isNotEmpty()) {
        agendamentos.forEach {
          it.status = SolicitacaoStatus.CONFIRMADO
        }
        agendamentoRepository.saveAll(agendamentos)
      }
      solicitacaoReservas.forEach {
        it.status = SolicitacaoStatus.CONFIRMADO
      }
      solicitacaoReservaRepository.saveAll(solicitacaoReservas)

    }

    if (!solicitacaoEquipamentos.isEmpty()) {
      val agendaSalaTipoEquipamentoList = agendaSalaTipoEquipamentoRepository.findByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos)
      if (!agendaSalaTipoEquipamentoList.isEmpty()) {
        agendaSalaTipoEquipamentoList.forEach {
          it.status = SolicitacaoStatus.CONFIRMADO
        }
        agendaSalaTipoEquipamentoRepository.saveAll(agendaSalaTipoEquipamentoList)
      }

      solicitacaoEquipamentos.forEach {
        it.status = SolicitacaoStatus.CONFIRMADO
      }
      solicitacaoEquipamentoRepository.saveAll(solicitacaoEquipamentos)
    }

    carrinho?.status = SolicitacaoStatus.CONFIRMADO
  }

  @Transactional
  fun encerrarCarrinhoPendente() {
/*
    val itens = carrinhoRepository.findByStatus(SolicitacaoStatus.PENDENTE)

     itens?.forEach { carrinho ->

       val d1 = carrinho.dateCreated

       val d2 = LocalDateTime.now()


       val diffSeconds = ChronoUnit.SECONDS.between(d1, d2)

       var sucesso = if (diffSeconds >= 300) true else false

       if (sucesso) {

         val solicitacaoAgendamentos = carrinho.solicitacoesReserva?.filter { it.refCarrinhoCompraEntity != null }
         val solicitacaoEquipamentos = carrinho.solicitacoesEquipamento?.filter { it.refCarrinhoCompraEntity != null }

         val agendamentos = if (!solicitacaoAgendamentos?.isEmpty()!!) agendamentoRepository.findAllByStatusAndSolicitacaoReservaIn(SolicitacaoStatus.PENDENTE, solicitacaoAgendamentos) else mutableListOf()
         if (!agendamentos.isEmpty()) {
           agendamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
           agendamentoRepository.saveAll(agendamentos)
         }

         if (!solicitacaoAgendamentos.isNullOrEmpty()) {
           solicitacaoAgendamentos?.forEach { sol -> sol.status = SolicitacaoStatus.CANCELADO }
           solicitacaoReservaRepository.saveAll(solicitacaoAgendamentos)
         }

         val equipamentos = if (!solicitacaoEquipamentos.isNullOrEmpty()) agendaSalaTipoEquipamentoRepository.findAllByStatusAndSolicitacaoEquipamentoIn(SolicitacaoStatus.PENDENTE, solicitacaoEquipamentos) else mutableListOf()
         if (!equipamentos?.isEmpty()) {
           equipamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
           agendaSalaTipoEquipamentoRepository.saveAll(equipamentos)
         }

         if (!solicitacaoEquipamentos.isNullOrEmpty()) {
           solicitacaoEquipamentos.forEach { it.status = SolicitacaoStatus.CANCELADO }
           solicitacaoEquipamentoRepository.saveAll(solicitacaoEquipamentos)
         }

         solicitacaoAgendamentos.filter { it.status == SolicitacaoStatus.CANCELAMENTO_PENDENTE }.forEach {
           it.status == SolicitacaoStatus.CONFIRMADO
           solicitacaoReservaRepository.save(it)

         }

         agendamentoRepository.findByStatus(SolicitacaoStatus.CANCELAMENTO_PENDENTE).forEach {
           it.status == SolicitacaoStatus.CONFIRMADO
           agendamentoRepository.save(it)
         }

         carrinho.status = SolicitacaoStatus.CANCELADO

         carrinhoRepository.save(carrinho)
       }
     }
*/
  }

  private fun calcularCredito(solicitacaoReservas: List<SolicitacaoReserva>, medico: Medico) {
    var agendamentos = listOf<Agendamento>()
    if (solicitacaoReservas?.isNotEmpty()!!)
      agendamentos = agendamentoService.findAllByStatusNotLikeAndSolicitacaoReservaIn(SolicitacaoStatus.CANCELADO, solicitacaoReservas)

    agendamentos.groupBy { it.dia?.format(DateTimeFormatter.ofPattern("MM/yyyy")) }.forEach { mesAno, agendamentosMes ->

      val dia1 = LocalDate.of(mesAno?.split('/')?.get(1)?.toInt()!!, mesAno?.split('/')?.get(0).toInt(), 1)
      val inicioMes = dia1.atStartOfDay()
      val fimMes = dia1.plusDays((dia1.lengthOfMonth() - 1).toLong()).atTime(23, 59)

      val mapAgendamentoHoras = agendamentoService.calcularHorasAgendadas(medico?.id!!, inicioMes, fimMes)

      var totalHorasPagas = 0
      mapAgendamentoHoras.values.forEach {
        totalHorasPagas = totalHorasPagas + it
      }

      val totalHorasSolicitadas = agendamentosMes.sumBy { ag -> ag.agendaSalas?.size!! }

      val totalHoras = totalHorasPagas + totalHorasSolicitadas

      val agendas: MutableList<Agendamento> = mutableListOf()
      agendas.addAll(agendamentosMes)
      agendas.addAll(mapAgendamentoHoras.keys.toMutableList())

      agendas.forEach { ag ->

        //BUSCA DESCONTO
        val descontos = descontoService.findVigenteAtivosByProfissionalAndDate(medico,ag.agendaSalas?.first()?.inicio!!)

        val descPeriodo = descontos.find { it.tipo == TipoDesconto.PERIODO }?.percentual?.div(100.00)
        val descHoraAvulsa = descontos.find { it.tipo == TipoDesconto.HORA_AVULSA }?.percentual?.div(100.00)

        //BUSCAR FAIXA PRECO PELA ESPECIALIDADE DA SALA
        var faixaPrecoList = faixaPrecoRepository
                  .findByTabelaPrecoAndTotalHorasGreaterThanEqualOrderByTotalHorasAsc(ag.agendaSalas?.first()?.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

        if (faixaPrecoList.isEmpty())
          faixaPrecoList = faixaPrecoRepository
                  .findByTabelaPrecoAndTotalHorasLessThanEqualOrderByTotalHorasDesc(ag.agendaSalas?.first()?.sala?.refEspecialidadeEntity?.tabelaPreco!!, totalHoras)

        val faixaPreco = if(descPeriodo != null) faixaPrecoList.first()?.valor?.minus( faixaPrecoList.first()?.valor!! * descPeriodo) else  faixaPrecoList.first()?.valor

        if (ag.agendaSalas!!.size > 2)
          custoOperacaoService.gerarCredito(ag, ag.custo, faixaPreco!!)
      }
    }
  }
}
