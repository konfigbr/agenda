package osahner.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestParam
import osahner.domain.Consulta
import osahner.domain.Paciente
import osahner.enums.LogEnum
import osahner.enums.SolicitacaoStatus
import osahner.enums.StatusConsulta
import osahner.repository.ConsultaRepository
import osahner.repository.MedicoRepository
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import osahner.domain.Medico
import osahner.domain.Sala
import osahner.dto.*
import osahner.repository.AgendamentoRepository
import osahner.repository.ParametroRepository
import osahner.utils.Utils
import java.time.LocalDate
import java.time.LocalTime

@Component
class ConsultaService(
        private val consultaRepository: ConsultaRepository,
        private val agendamentoService: AgendamentoService,
        private val userService: UserService,
        private val pacienteService: PacienteService,
        private val medicoRepository: MedicoRepository,
        private val emailService: EmailService,
        private val auditLogService: AuditLogService,
        private val medicoService: MedicoService,
        private val agendamentoRepository: AgendamentoRepository,
        private val parametroRepository: ParametroRepository
) {
    fun list(): MutableList<Consulta> = consultaRepository.findAll()

    fun findByMedico(medicoId: Long): MutableList<Consulta> {
        val medico = medicoService.findById(medicoId).orElseThrow { RuntimeException("Profissional não encontrado") }
        return findByMedico(medico)
    }

    fun findByMedico(medico: Medico): MutableList<Consulta> = consultaRepository.findByRefMedicoEntity(medico)

    fun  listMyMedicalAppointments(dataInicio: LocalDateTime?, dataFim: LocalDateTime?, salaId: Long?, status: StatusConsulta?, pacienteId: Long?)
        : MutableList<Consulta> {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val medicoId = medicoService.findByrefUserEntity(currentUser).orElseThrow { RuntimeException("Profissional não encontrado") }.id

        return listMedicalAppointments(medicoId, dataInicio, dataFim, salaId, status, pacienteId)
    }

    fun listMedicalAppointments(medicoId: Long?, dataInicio: LocalDateTime?, dataFim: LocalDateTime?, salaId: Long?, status: StatusConsulta?, pacienteId: Long?)
        : MutableList<Consulta> {
        return consultaRepository.findByCriteria(medicoId, dataInicio, dataFim, salaId, status, pacienteId)
    }

    fun findById(id: Long): Consulta? = consultaRepository.findById(id).orElseThrow { RuntimeException("Consulta não encontrada") }

    fun findByStatus(status: StatusConsulta) : MutableList<Consulta> = consultaRepository.findByStatus(status)

    fun findByPatientLoggedAndProfessional(idProfessional: Long, idPaciente: Long? = null): MutableList<Consulta> {
        val professional = medicoRepository.findById(idProfessional).get()

        //val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val patient = if (idPaciente !== null) pacienteService.findById(idPaciente).get() else pacienteService.findByrefUserEntity(userService.findByUsername(userService.getCurrentUserEmail()).get()).get()

        if (patient == null)
            throw RuntimeException("Paciente não encontrado")

        if (professional == null)
            throw RuntimeException("Profissional não encontrado")

        return consultaRepository.findByRefPacienteEntityAndRefMedicoEntityOrderByDataInicioDesc(patient, professional)
    }

    fun cancelAppointment(idAppointment: Long): ConsultaDto {
        val appointment = consultaRepository.findById(idAppointment).get()

        if (appointment == null)
            throw RuntimeException("Consulta não encontrada")

        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val horatual = LocalDateTime.now(saoPauloZone)

        if (horatual > appointment.dataInicio && horatual < appointment.dataFim)
            throw RuntimeException("Não é possível cancelar uma consulta em andamento")
        else if (horatual > appointment.dataInicio)
            throw RuntimeException("Não é possivel cancelar uma consulta já realizada")

        if (appointment.status === StatusConsulta.CANCELADA)
            throw RuntimeException("Consulta já está cancelada")

        appointment.status = StatusConsulta.CANCELADA

        //Envia email para profissional
        emailService.cancelamentoConsulta(appointment.refMedicoEntity?.user?.username, appointment)

        //Gera Log
        this.gerarLogCancelamentoConsulta(appointment)

        return consultaRepository.save(appointment).toDTO()
    }

    private fun gerarLogCancelamentoConsulta(appointment: Consulta) {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

        val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        val dia = formatterData.format(appointment.dataInicio)

        val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
        val inicio = formatterHora.format(appointment.dataInicio)
        val fim = formatterHora.format(appointment.dataFim)

        val nomeProfissional = appointment.refMedicoEntity?.user?.nome
        val nomePaciente = appointment.refPacienteEntity?.user?.nome

        val descricaoLog = "Cancelada consulta do paciente " + nomePaciente +
                ", agendada com o profissional " + nomeProfissional +
                " para o dia " + dia + ", das " + inicio + " ás " + fim
        auditLogService.criarLog(currentUser, LogEnum.CANCELAR_CONSULTA, descricaoLog)

    }

    fun findByAgendamentoId(agendamentoId: Long): MutableList<Consulta> {
        val agendamento = agendamentoService.findById(agendamentoId).orElseThrow { RuntimeException("Agendamento não encontrado") }
        return consultaRepository.findByRefAgendamentoEntity(agendamento)
    }

    @Transactional
    fun saveMedicalAppointment(medicoId: Long, consultaDto: CreateConsultaDto, paciente: Paciente): Consulta {
        val now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))

        if (consultaDto.dataInicio!! < now) {
            throw RuntimeException("A data da consulta não pode ser inferior a data atual")
        }

        if (consultaDto.dataFim!! <= consultaDto.dataInicio || consultaDto.dataInicio!! >= consultaDto.dataFim) {
            throw RuntimeException("As datas de início e fim da consulta não são válidas")
        }

        val medico = medicoService.findById(medicoId).orElseThrow { RuntimeException("Profissional não encontrado") }
        val agendamento = agendamentoService.findById(consultaDto.agendamentoId!!)
                .orElseThrow { RuntimeException("Agendamento não encontrado") }

        if (agendamento.status!! != SolicitacaoStatus.CONFIRMADO || agendamento.medico?.id!! != medico.id!!) {
            throw  RuntimeException("O agendamento da sala não está mais disponível. Entre em contato com o profissional responsável")
        }

        // Verifica se a consulta está dentro do horário de agendamento da sala
        agendamento.agendaSalas?.sortBy { it.fim }

        val inicioAgendamentoSala = agendamento.agendaSalas?.first()?.inicio
        val fimAgendamentoSala = agendamento.agendaSalas?.last()?.fim

        if (!(consultaDto.dataInicio!! >= inicioAgendamentoSala && consultaDto.dataFim!! <= fimAgendamentoSala)) {
            throw RuntimeException("A sala não está disponível para este horário")
        }

        if (!hasHorarioDisponivel(medico, consultaDto.dataInicio!!, consultaDto.dataFim!!)) {
            throw RuntimeException("O medico ${medico.user!!.nome} não tem agenda disponível para esse horário")
        }

        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val profissional = medicoService.findByrefUserEntity(currentUser)

        // Salva a consulta no banco de dados
        val consultaCriada = consultaDto.toConsulta().apply {
            id = null
            version = 1
            refPacienteEntity = paciente
            refMedicoEntity = medico
            refAgendamentoEntity = agendamento
            status = if (profissional.isPresent) StatusConsulta.APROVADA else StatusConsulta.SOLICITADA
        }.let { consultaRepository.saveAndFlush(it) }

        // Envia e-mail para o profissional
        val assuntoEmail = "Novo agendamento - ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))} - ${paciente?.user?.nome}"

        emailService.sendEmail(medico?.user?.username, assuntoEmail, getNovaConsultaEmailBody(consultaCriada))

        // Gera auditoria
        val descricaoLog = "Consulta agendada para ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                "das ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} até as ${consultaCriada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "com o profissional ${medico.user?.nome}"

        auditLogService.criarLog(paciente.user!!, LogEnum.NOVA_CONSULTA, descricaoLog)

        return consultaCriada
    }

    @Transactional
    fun editMedicalAppointment(medicoId: Long, consultaDto: CreateConsultaDto, paciente: Paciente, consultaId: Long): Consulta {
        val now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))

        if (consultaDto.dataInicio!! < now) {
            throw RuntimeException("A data da consulta não pode ser inferior a data atual")
        }

        if (consultaDto.dataFim!! <= consultaDto.dataInicio || consultaDto.dataInicio!! >= consultaDto.dataFim) {
            throw RuntimeException("As datas de início e fim da consulta não são válidas")
        }
        val medico = medicoService.findById(medicoId).orElseThrow { RuntimeException("Profissional não encontrado") }
        val agendamento = agendamentoService.findById(consultaDto.agendamentoId!!)
                .orElseThrow { RuntimeException("Agendamento não encontrado") }

        if (agendamento.status!! != SolicitacaoStatus.CONFIRMADO || agendamento.medico?.id!! != medico.id!!) {
            throw  RuntimeException("O agendamento da sala não está mais disponível. Entre em contato com o profissional responsável")
        }

        // Verifica se a consulta está dentro do horário de agendamento da sala
        agendamento.agendaSalas?.sortBy { it.fim }

        val inicioAgendamentoSala = agendamento.agendaSalas?.first()?.inicio
        val fimAgendamentoSala = agendamento.agendaSalas?.last()?.fim

        if (!(consultaDto.dataInicio!! >= inicioAgendamentoSala && consultaDto.dataFim!! <= fimAgendamentoSala)) {
            throw RuntimeException("A sala não está disponível para este horário")
        }

        if (!hasHorarioDisponivel(medico, consultaDto.dataInicio!!, consultaDto.dataFim!!)) {
            throw RuntimeException("O medico ${medico.user!!.nome} não tem agenda disponível para esse horário")
        }

        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val profissional = medicoService.findByrefUserEntity(currentUser)

        // Salva a consulta no banco de dados
        val consultaCriada = consultaDto.toConsulta().apply {
            id = consultaId
            version = 1
            refPacienteEntity = paciente
            refMedicoEntity = medico
            refAgendamentoEntity = agendamento
            status = if (profissional.isPresent) StatusConsulta.REAGENDADA else StatusConsulta.SOLICITADA
        }.let { consultaRepository.saveAndFlush(it) }

        // Envia e-mail para o profissional
        val assuntoEmail = "Novo agendamento - ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))} - ${paciente?.user?.nome}"

        emailService.sendEmail(medico?.user?.username, assuntoEmail, getNovaConsultaEmailBody(consultaCriada))

        // Gera auditoria
        val descricaoLog = "Consulta agendada para ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                "das ${consultaCriada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} até as ${consultaCriada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "com o profissional ${medico.user?.nome}"

        auditLogService.criarLog(paciente.user!!, LogEnum.NOVA_CONSULTA, descricaoLog)

        return consultaCriada

    }

    private fun hasHorarioDisponivel(medico: Medico, dataInicio: LocalDateTime, dataFim: LocalDateTime): Boolean {
        val inicioDia = LocalDateTime.of(dataInicio.year, dataInicio.month, dataInicio.dayOfMonth, 0, 0, 0)
        val fimDia = LocalDateTime.of(dataFim.year, dataFim.month, dataFim.dayOfMonth, 23, 59, 59)
        val consultas = consultaRepository.findByRefMedicoEntityAndInicioAndFim(medico.id, inicioDia, fimDia)
        val status = listOf(StatusConsulta.SOLICITADA, StatusConsulta.APROVADA, StatusConsulta.CONFIRMADA)

        return !consultas!!.filter { status.contains(it.status) }.any {
            (dataInicio >= it.dataInicio && dataFim <= it.dataFim) || (dataFim > it.dataInicio && dataInicio < it.dataFim)
        }
    }

    private fun getNovaConsultaEmailBody(consulta: Consulta): String {
        val medico = consulta.refMedicoEntity?.user
        val paciente = consulta.refPacienteEntity?.user
        val dataInicio = consulta.dataInicio
        val dataFim = consulta.dataFim

        val body = "<p>Prezado(a) Doutor(a) ${medico?.nome}</p>" +
                "<p>Segue informações do novo agendamento solicitado:</p> <br/>" +
                "<b>Paciente:</b> ${paciente?.nome} <br/>" +
                "<b>Data:</b> ${dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} <br/>" +
                "<b>Horário:</b> ${dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))}<br/>" +
                "<b>Motivo:</b> ${consulta.motivo} <br/><br/>" +
                "<b>Atenção:</b> Esta solicitação de agendamento está pendente aprovação. O paciente só terá a confirmação do agendamento da consulta após " +
                "sua Aprovação. <b>Clique no link abaixo e aprove agora mesmo!</b> <br/><br/>" +
                "<a href='http://54.94.48.182/#/auth/login'>Acesso ao sistema<a/>"

        return body
    }
  
    fun findHorariosOcupadosByProfissional(profissional: Medico, inicio: LocalDateTime, fim: LocalDateTime): Any? =
            consultaRepository.findByRefMedicoEntityAndInicioAndFim(profissional.id, inicio, fim)
                    ?.sortedBy { it.dataInicio }?.map {
                        HorariosConsultaDto (
                                idAgendamento = it.refAgendamentoEntity?.id,
                                idConsulta = it.id,
                                dataInicio = it.dataInicio,
                                dataFim = it.dataFim
                        )
                    }

    fun findHorariosDisponiveisByProfissional(profissional: Medico, inicio: LocalDateTime, fim: LocalDateTime): Any? {
        val agendamentos = agendamentoRepository.findAllByMedicoAndInicioAndFim(profissional.id, inicio, fim)
        var horariosDisponiveis = mutableListOf<HorarioDisponivelConsultaDto>()
        val horariosOcupados = consultaRepository.findByRefMedicoEntityAndInicioAndFim(profissional.id, inicio, fim)

        horariosOcupados?.groupBy { it.refAgendamentoEntity }?.forEach { agendamento, consultas ->
            consultas.sortedBy { it.dataInicio }

            if (agendamento?.agendaSalas?.first()?.inicio != consultas?.first()?.dataInicio)
                horariosDisponiveis.add( HorarioDisponivelConsultaDto (
                    dataInicio = agendamento?.agendaSalas?.first()?.inicio,
                    dataFim = consultas?.first()?.dataInicio
                ))

            var consultaAux: Consulta? = null
            consultas.forEach {

                if (consultaAux != null) {

                    if (consultaAux?.dataFim != it.dataInicio )
                        horariosDisponiveis.add( HorarioDisponivelConsultaDto (
                            dataInicio = consultaAux?.dataFim,
                            dataFim = it.dataInicio
                        ))
                }

                consultaAux = it
            }

            if (agendamento?.agendaSalas?.last()?.fim != consultas?.last()?.dataFim)
                horariosDisponiveis.add( HorarioDisponivelConsultaDto (
                    dataInicio = consultas?.last()?.dataFim,
                    dataFim = agendamento?.agendaSalas?.last()?.fim
                ))

            agendamentos.remove(agendamento)
        }

        agendamentos.forEach {
            horariosDisponiveis.add( HorarioDisponivelConsultaDto (
                dataInicio = it.agendaSalas?.first()?.inicio,
                dataFim = it.agendaSalas?.last()?.fim
            ))
        }

        return horariosDisponiveis
    }

    fun findWithLimite(): MutableList<Consulta>? {
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone).with(LocalTime.of(0,0,0))

        var numDias = 3
        val parametro = parametroRepository.findByTipo("NUMERO_DIAS_CONSULTAS")

        if (parametro !== null)
            numDias = parametro.valor?.toInt()!!

        val dataLimite = now.minusDays(numDias.toLong())

        return consultaRepository.findByNumeroDias(dataLimite)
    }

    fun findWithLimiteByProfissional(): MutableList<Consulta>? {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val medico = medicoService.findByrefUserEntity(currentUser).get()

        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone).with(LocalTime.of(0,0,0))

        var numDias = 3
        val parametro = parametroRepository.findByTipo("NUMERO_DIAS_CONSULTAS")

        if (parametro !== null)
            numDias = parametro.valor?.toInt()!!

        val dataLimite = now.minusDays(numDias.toLong())

        return consultaRepository.findByNumeroDiasAndMedico(dataLimite, medico.id)
    }

    fun findSolicitadaByProfissional(): MutableList<Consulta>? {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val medico = medicoService.findByrefUserEntity(currentUser).get()

        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone)

        return consultaRepository.findSolicitadaByMedico(now, medico.id)
    }

    fun findSolicitadas(): MutableList<Consulta>? {
        val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
        val now = LocalDateTime.now(saoPauloZone)

        return consultaRepository.findSolicitadas(now)
    }

    fun existsById(id: Long) = consultaRepository.existsById(id)

    fun changeStatus(idAppointment: Long, dto: TrocaStatusConsultaDto): ConsultaDto? {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val consulta = consultaRepository.findById(idAppointment).get()

        if (consulta.status == StatusConsulta.CONFIRMADA
                || consulta.status == StatusConsulta.REAGENDADA
                || consulta.status == StatusConsulta.CANCELADA)
            throw RuntimeException("Não é possível aterar o status desta consulta")

        consulta.status = dto.status

        var consultaAtualizada: Consulta? = null
        when (dto.status) {
            StatusConsulta.APROVADA -> {
                consultaAtualizada = consultaRepository.save(consulta)

                emailService.aprovacaoConsulta(consultaAtualizada.refPacienteEntity?.user?.username, consultaAtualizada)

                val descricaoLog = "Consulta aprovada para ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                        "das ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consultaAtualizada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                        "com o profissional ${consultaAtualizada.refMedicoEntity?.user?.nome} e paciente ${consultaAtualizada.refPacienteEntity?.user?.nome}"

                auditLogService.criarLog(currentUser, LogEnum.APROVACAO_CONSULTA, descricaoLog)
            }
            StatusConsulta.CONFIRMADA -> {
                consultaAtualizada = consultaRepository.save(consulta)

                val descricaoLog = "Consulta confirmada para ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                        "das ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consultaAtualizada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                        "com o profissional ${consultaAtualizada.refMedicoEntity?.user?.nome} e paciente ${consultaAtualizada.refPacienteEntity?.user?.nome}"

                auditLogService.criarLog(currentUser, LogEnum.CONFIRMACAO_CONSULTA, descricaoLog)
            }
            StatusConsulta.NAO_CONFIRMADA -> {
                consultaAtualizada = consultaRepository.save(consulta)

                val descricaoLog = "Consulta não confirmada para ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                        "das ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consultaAtualizada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                        "com o profissional ${consultaAtualizada.refMedicoEntity?.user?.nome} e paciente ${consultaAtualizada.refPacienteEntity?.user?.nome}"

                auditLogService.criarLog(currentUser, LogEnum.NAO_CONFIRMACAO_CONSULTA, descricaoLog)
            }
            StatusConsulta.CANCELADA -> {
                this.cancelAppointment(consulta.id!!)
            }
            StatusConsulta.REAGENDADA -> {
                consultaAtualizada = consultaRepository.save(consulta)
            }
            StatusConsulta.NAO_APROVADA -> {
                if (dto.observacao != null)
                    consulta.observacao = dto.observacao

                consultaAtualizada = consultaRepository.save(consulta)

                val descricaoLog = "Consulta não aprovada para ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                        "das ${consultaAtualizada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consultaAtualizada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                        "com o profissional ${consultaAtualizada.refMedicoEntity?.user?.nome} e paciente ${consultaAtualizada.refPacienteEntity?.user?.nome}"

                auditLogService.criarLog(currentUser, LogEnum.NAO_APROVACAO_CONSULTA, descricaoLog)
            }
            else -> {
                return throw RuntimeException("Status inválido")
            }
        }

        return consultaAtualizada?.toDTO()
    }

    @Transactional
    fun reschedulingMedicalAppointment(idConsulta: Long, consultaDto: CreateConsultaDto): Consulta {
        val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
        val consulta = consultaRepository.findById(idConsulta).get()

        consulta.status = StatusConsulta.REAGENDADA


        val consultaReagendada = editMedicalAppointment(consulta.refMedicoEntity?.id!!, consultaDto, consulta.refPacienteEntity!!, idConsulta)

        emailService.reagendamentoConsulta(consulta.refPacienteEntity?.user?.username, consultaReagendada)

        val descricaoLog = "Consulta reagendada de ${consulta.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                "das ${consulta.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consulta.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} "+
                "para ${consultaReagendada.dataInicio?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} " +
                "das ${consultaReagendada.dataInicio?.format(DateTimeFormatter.ofPattern("HH:mm"))} às ${consultaReagendada.dataFim?.format(DateTimeFormatter.ofPattern("HH:mm"))} " +
                "com o profissional ${consultaReagendada.refMedicoEntity?.user?.nome} e paciente ${consultaReagendada.refPacienteEntity?.user?.nome}"
        auditLogService.criarLog(currentUser, LogEnum.REAGENDAMENTO_CONSULTA, descricaoLog)

        return consultaReagendada
    }
}