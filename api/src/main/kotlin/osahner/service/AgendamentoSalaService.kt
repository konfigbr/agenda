package osahner.service

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.domain.*
import osahner.dto.*
import osahner.enums.PeriodoAgendamento
import osahner.enums.SolicitacaoStatus
import osahner.enums.TipoReserva
import osahner.repository.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.*

@Component
@Transactional
class AgendamentoSalaService (
        private val agendaSalaRepository: AgendaSalaRepository,
        private val agendaAssistenteRepository: AgendaAssistenteRepository,
        private val agendamentoMedicoService: AgendamentoMedicoService,
        private val salaRepository: SalaRepository,
        private val assistenteRepository: AssistenteRepository,
        private val userService: UserService,
        private val agendamentoRepository: AgendamentoRepository,
        private val medicoService: MedicoService

) {
  fun list(): Collection<AgendaSala> = agendaSalaRepository.findAll()

  fun findById(id: Long?): Optional<AgendaSala> = agendaSalaRepository.findById(id)

  fun save(dto: AgendaSalaDto): AgendaSala = agendaSalaRepository.saveAndFlush(AgendaSala.fromDTO(dto))

  fun delete(id: Long) = agendaSalaRepository.deleteById(id)

  fun alter(id: Long, sala: AgendaSalaDto): AgendaSala {
    var safeAgendamentoSala = sala.copy(id = id)
    return return save(safeAgendamentoSala)
  }

  fun existsById(id: Long) = agendaSalaRepository.existsById(id)

  fun validarHora(hora: Int, diaSemana: Int): Boolean {

    val dayOfWeek: DayOfWeek = DayOfWeek.of(diaSemana)

    if (dayOfWeek == DayOfWeek.SUNDAY) {
      return false
    }

    if (hora < 8 || hora > 21 || hora == 12 || hora == 17) {
      return false
    }

    if (dayOfWeek == DayOfWeek.SATURDAY && hora > 11) {
      return false
    }

    return true

  }

  @Transactional(readOnly = true)
  fun listarSalasDisponiveis(): MutableList<SalaDisponivelDto> {

    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val medico = medicoService.findByrefUserEntity(currentUser)

    var salas = salaRepository.findAllOrderByEspecialidadeAndId()

    var salasDisponiveisDto: MutableList<SalaDisponivelDto> = arrayListOf()
    var salasDisponiveis: MutableCollection<Sala>

    if (medico.isPresent){
      salasDisponiveis = salas.filter { it.refEspecialidadeEntity?.id === medico.get().refEspecialidadeEntity?.id }.toMutableList()
      salasDisponiveis.addAll(salas.filter { it.refEspecialidadeEntity?.id !== medico.get().refEspecialidadeEntity?.id }.toMutableList())
    } else {
      salasDisponiveis = salas
    }

    salasDisponiveis.forEach {

      var salaDisponivel = SalaDisponivelDto(it.toDTO(), null)
      salasDisponiveisDto.add(salaDisponivel)
    }

    return salasDisponiveisDto

    /*var salasDisponiveisDto: MutableList<SalaDisponivelDto> = arrayListOf()
    var salasDisponiveis: MutableCollection<Sala> = salaRepository.findAllByAvailable()

    salasDisponiveis.forEach {
      var assistentes = agendaAssistenteRepository.findAssistentePrincipalBySala(it.id)
      if (!assistentes?.isEmpty()) {
        var salaDisponivel = SalaDisponivelDto(it.toDTO(), assistentes.get(0).assistente?.toCustomDTO())
        salasDisponiveisDto.add(salaDisponivel)
      } else {
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        var agenda = agendaSalaRepository.findByInicioGreaterThanEqualsAndSala(it.id, LocalDateTime.now(saoPauloZone))
        if (!assistentes?.isEmpty()) {
          var salaDisponivel = SalaDisponivelDto(it.toDTO(), agenda.get(0).assistente?.toCustomDTO())
          salasDisponiveisDto.add(salaDisponivel)
        }
      }
    }
    return salasDisponiveisDto*/
  }

  fun agendarAssistente(requestData: AgendaAssistenteRequestDto): Any? {
    val assistente = assistenteRepository.findById(requestData.assistente?.id).get()
    val possuiAgendamento = possuiAgendamento(requestData)

    if (assistente.virtual != true && possuiAgendamento)
      return throw RuntimeException("O assistente selecionado já possui um agendamento no período selecionado")

    var agendasSalaExistentes = agendaSalaRepository.findAllBySalaAndInicioGreaterThanEqualAndFimLessThanEqual(requestData.sala!!, requestData.inicio!!, requestData.fim!!)
    var novasAgendasSala = mutableListOf<AgendaSala>()

    requestData.inicio = requestData.inicio!!.withMinute(0).withSecond(0).withNano(0)
    requestData.fim = requestData.fim!!.withMinute(0).withSecond(0).withNano(0)

    while (requestData.inicio!! < requestData.fim) {
      var agendaSala = agendasSalaExistentes?.find{ it.inicio?.isEqual(requestData.inicio)!! }

      if (agendaSala == null) {

        var novaAgendasSala = AgendaSala()
        novaAgendasSala.custo = 0.0
        novaAgendasSala.sala = requestData.sala
        novaAgendasSala.assistente = assistente
        novaAgendasSala.inicio = requestData.inicio
        novaAgendasSala.fim = novaAgendasSala.inicio?.plusHours(1)
        novaAgendasSala.fixo = false

        novasAgendasSala.add(novaAgendasSala)
      } else {
        agendaSala?.fixo = false
        agendaSala?.assistente = assistente
      }
      requestData.inicio = requestData.inicio?.plusHours(1)
    }
    agendasSalaExistentes?.addAll(novasAgendasSala)
    return agendaSalaRepository.saveAll(agendasSalaExistentes)
  }

  private fun possuiAgendamento(requestData: AgendaAssistenteRequestDto): Boolean =
    agendaSalaRepository.findByAssistenteAndInicioGreaterThanEqualsAndFimLessThanEqualsAndConfirmed(requestData.assistente?.id!!, requestData.inicio!!, requestData.fim!!) > 0

  @Transactional(readOnly = true)
  fun getAgendaCompleta(sala: Sala, inicio: LocalDate, fim: LocalDate): AgendaCompletaSalaReturnDto? {

    var agendaAssistenteRes = mutableListOf<AgendaCompletaDto>()

    var agendamentosRes = agendamentoMedicoService.listarAgendamentos(sala.id!!, inicio.atStartOfDay().toString(), fim.atTime(23, 59).toString())
    val periodosAgenda = this.agruparAgendaSala(sala, inicio, fim)

    if (periodosAgenda.isNotEmpty())
      agendamentosRes.addAll(periodosAgenda.map {AgendamentoCustomDto.fromDto(it)})

    var agendamentosPorDia: Map<LocalDate, MutableList<AgendamentoCustomDto>> = agendamentosRes.groupBy { it.inicio!!.toLocalDate() } as Map<LocalDate, MutableList<AgendamentoCustomDto>>
    var currentInicio = inicio

    while (currentInicio <= fim) {
      var horaInicial = 8

      if (currentInicio.dayOfWeek == DayOfWeek.SUNDAY)
        currentInicio = currentInicio.plusDays(1)

      var agendamentosDia = agendamentosPorDia.get(currentInicio)
      agendamentosDia?.sortBy { it.inicio }

      while (horaInicial < 22) {
        var horaMinimaAgendada = agendamentosDia?.minBy { it.inicio!!.hour }?.inicio?.hour ?: 22

        if (horaInicial < horaMinimaAgendada) {
          var agendaAssistente = agendaAssistenteRepository.findAllBySalaAndDiaDaSemanaAndHoraDoDiaGreaterThanEqualAndHoraDoDiaLessThanOrderByHoraDoDiaAsc(sala, currentInicio.dayOfWeek.ordinal+1, horaInicial, horaMinimaAgendada)
          agendaAssistente.groupBy { it.groupId }.forEach { grupo, agendaAssistentePorGrupo ->

            val dto = AgendaCompletaDto(
              groupId = grupo,
              assistente = AgendaCompletaAssistenteDto(id=agendaAssistentePorGrupo.first().assistente?.id, nome=agendaAssistentePorGrupo.first().assistente?.user?.nome),
              sala = AgendaCompletaSalaDto(id=sala.id, nome=sala.nome ),
              horaInicio = agendaAssistentePorGrupo.minBy { it.horaDoDia!! }?.horaDoDia,
              horaFim = agendaAssistentePorGrupo.maxBy { it.horaDoDia!! }?.horaDoDia,
              diaDaSemana = agendaAssistentePorGrupo.first().diaDaSemana
            )
            agendaAssistenteRes.add(dto)
          }
        }

        if (!agendamentosDia.isNullOrEmpty())
          horaInicial = agendamentosDia.removeAt(0).fim!!.hour
        else
          horaInicial = horaMinimaAgendada
      }
      currentInicio = currentInicio.plusDays(1)
    }

    return AgendaCompletaSalaReturnDto(
      agendamentos = agendamentosRes,
      agendaAssistente = agendaAssistenteRes

    )
  }

  fun getHorariosAssistentes(agendaCompleta: AgendaCompletaSalaReturnDto): MutableList<HorarioAssistenteDto> {

    var horarios = mutableListOf<HorarioAssistenteDto>()
    var horariosOrdenados = mutableListOf<HorarioAssistenteDto>()

    var agendaAssistente =  agendaCompleta.agendaAssistente
    var agendamentos = agendaCompleta.agendamentos

    agendamentos?.sortBy { it.dia }
    agendamentos?.sortBy { it.inicio?.dayOfWeek }
    agendaCompleta.agendaAssistente?.sortBy { it.diaDaSemana }

    agendamentos?.forEach {
      var horario = HorarioAssistenteDto (
        assistente = it.assistente?.nome,
        dia = it.inicio?.dayOfWeek.toString(),
        inicio = it.inicio?.toLocalTime(),
        fim = it.fim?.toLocalTime()
      )
      horarios.add(horario)
    }
    agendaAssistente?.sortBy { it.diaDaSemana }
    agendaAssistente?.forEach {
      var horario = HorarioAssistenteDto (
        assistente = it.assistente?.nome,
        dia = DayOfWeek.of(it.diaDaSemana!!).toString(),
        inicio = LocalTime.of(it.horaInicio!!, 0),
        fim =  LocalTime.of(it.horaFim!!, 0)
      )

      horarios.add(horario)
    }

    horarios.groupBy { it.dia }.forEach { (dia, u) ->
      val horarioAssistenteDto = u.sortedBy { it.inicio }

      horariosOrdenados.addAll(horarioAssistenteDto)
    }

    return horariosOrdenados
  }

  private fun agruparAgendaSala(sala: Sala, inicio: LocalDate, fim: LocalDate): MutableList<AgendamentoDto> {
    var periodosAgenda = mutableListOf<AgendamentoDto>()
    var listaAtual = mutableListOf<AgendaSala>()
    var agendasSala = agendaSalaRepository.findBySalaAndInicioAndFimAndAgendamentoNull(sala.id,inicio.atStartOfDay(),fim.atTime(23, 59))

    agendasSala?.forEachIndexed { index, agendaSala ->
      if (index == 0) {
        listaAtual.add(agendaSala)
      } else {
        val agendaSalaAnterior = agendasSala?.get(index - 1)
        if (ChronoUnit.HOURS.between(agendaSalaAnterior.inicio, agendaSala.inicio).toInt() == 1 && agendaSalaAnterior.assistente!!.equals(agendaSala.assistente)) {
          listaAtual.add(agendaSala)
        } else {
          periodosAgenda.add(AgendamentoDto(sala=sala, assistente=agendaSalaAnterior.assistente?.toCustomDTO(), inicio= listaAtual.minBy { it.inicio!! }?.inicio, fim=listaAtual.maxBy { it.fim!! }?.fim, tiposEquipamentos= mutableListOf()))
          listaAtual = mutableListOf(agendaSala)
        }
      }
    }

    if (listaAtual.isNotEmpty())
      periodosAgenda.add(AgendamentoDto(sala=sala, assistente=listaAtual[0].assistente?.toCustomDTO(), inicio=listaAtual.minBy { it.inicio!! }?.inicio, fim=listaAtual.maxBy { it.fim!! }?.fim, tiposEquipamentos=mutableListOf()))

    return periodosAgenda
  }


  @Transactional
  fun agendarManutencao(dto: ManutencaoDto): Any? {
    //PEGA ASSISTENTE MANUTENÇÃO
    val assistenteManutencao = assistenteRepository.findByManutencao().get()

    if (assistenteManutencao == null)
      return throw RuntimeException("Não há assistente de manutenção registrado")

    val sala = salaRepository.findById(dto.sala).get()

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")
    val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)

    var inicio = dto.inicio!!.withMinute(0).withSecond(0).withNano(0)
    var fim = dto.fim!!.withMinute(0).withSecond(0).withNano(0)

    val periodoAgendamento = PeriodoAgendamento.fromInt(dto.periodoAgendamento!!)

    var agendamento = Agendamento()
    agendamento.custo = 0.0
    agendamento.medico = null
    agendamento.status = SolicitacaoStatus.CONFIRMADO
    agendamento.periodo = periodoAgendamento
    agendamento.agendaSalas = arrayListOf()
    agendamento.dateCreated = now
    agendamento.solicitacaoReserva = null
    agendamento.tipoReserva = TipoReserva.MANUTENCAO

    var agendaSalaAssistente: MutableList<AgendaSala> = mutableListOf()

    val horarios = (inicio.hour) until fim.hour
    horarios.forEach { hora ->

      var filtroInicial = inicio.withHour(hora).withMinute(0).withSecond(0).withNano(0)

      var filtroFinal = inicio.withHour(hora + 1).withMinute(0).withSecond(0).withNano(0)

      var agendaSala = agendaSalaRepository.findBySalaAndInicioAndFim(dto.sala, filtroInicial, filtroFinal)

      var inicioTmp = inicio.withHour(hora ).withMinute(0).withSecond(0).withNano(0)

      var fimTmp = inicio.withHour(hora + 1).withMinute(0).withSecond(0).withNano(0)

      if (agendaSala == null) {
        agendaSala = AgendaSala()
        agendaSala.sala = sala
        agendaSala.fixo = false
        agendaSala.inicio = inicioTmp
        agendaSala.fim = fimTmp
        agendaSala.custo = 0.0
        agendaSala.assistente = assistenteManutencao
      }

      if (agendaSala != null) {
        agendaSala.assistente = assistenteManutencao
        agendaSalaAssistente.add(agendaSala)
      }
    }

    var agendaDisponivel1 = agendaSalaAssistente
    var (agendaDisponivel2, agendaComAgendamento) = agendaDisponivel1.partition { it.agendamento == null || it.agendamento!!.status == SolicitacaoStatus.CANCELADO }

    var agendaDisponivel: MutableList<AgendaSala> = mutableListOf()

    agendaDisponivel2.forEach {
      agendaDisponivel.add(it)
    }

    agendaComAgendamento.groupBy { it.agendamento }.forEach { ag, agenda ->
      throw RuntimeException("A sala ${agenda.first().sala?.nome} está agendada para o médico ${ag?.medico?.user?.nome} no dia ${agenda.first().inicio?.format(dateFormatter)} entre ${agenda.first().inicio?.format(hourFormatter)} e ${agenda.last().fim?.format(hourFormatter)}")
    }

    agendaDisponivel.forEach {
      it.agendamento = agendamento
      agendamento.agendaSalas!!.add(it)
    }

    agendamento.dia = agendaDisponivel.first().inicio!!.toLocalDate()

    return agendamentoRepository.save(agendamento)
  }
}





