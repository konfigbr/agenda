package osahner.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody
import osahner.config.SecurityProperties
import osahner.domain.Agendamento
import osahner.domain.HistoricoAgendamentoStatus
import osahner.domain.Medico
import osahner.domain.User
import osahner.dto.AgendamentoDto
import osahner.dto.CheckinDto
import osahner.dto.HistoricoAgendamentoStatusDto
import osahner.enums.AgendamentoStatus
import osahner.enums.LogEnum
import osahner.enums.StatusMedico
import osahner.repository.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
@Transactional
class HistoricoAgendamentoStatusService(
  private val historicoAgendamentoStatusRepository: HistoricoAgendamentoStatusRepository,
  private val agendamentoService: AgendamentoService,
  private val agendamentoRepository: AgendamentoRepository,
  private val medicoRepository: MedicoRepository,
  private val assistenteRepository: AssistenteRepository,
  private val securityProperties: SecurityProperties,
  private val userRepository: UserRepository,
  private val  medicoService: MedicoService,
  private val  assistenteService: AssistenteService,
  private val auditLogService: AuditLogService
  ) {
  fun list(): Collection<HistoricoAgendamentoStatus> = historicoAgendamentoStatusRepository.findAll()

  fun findById(id: Long?): Optional<HistoricoAgendamentoStatus> = historicoAgendamentoStatusRepository.findById(id)

  fun save(dto: HistoricoAgendamentoStatusDto): HistoricoAgendamentoStatus = historicoAgendamentoStatusRepository.saveAndFlush(HistoricoAgendamentoStatus.fromDTO(dto))

  fun delete(id: Long) = historicoAgendamentoStatusRepository.deleteById(id)

  fun alter(id: Long, historicoAgendamentoStatus: HistoricoAgendamentoStatusDto): HistoricoAgendamentoStatus {
    var safeHistoricoAgendamentoStatus = historicoAgendamentoStatus.copy(id = id)
    return return save(safeHistoricoAgendamentoStatus)
  }

  fun existsById(id: Long) = historicoAgendamentoStatusRepository.existsById(id)

  fun confirmaProfissional(idAgendamentoSala: Long?, idUser: Long?): Boolean {
    var agendamento = agendamentoService.findById(idAgendamentoSala).get()

    if (idUser != agendamento?.medico?.id)
      return false

    return true
  }


  fun checkinDuplicadoProfissional(idAgendamentoSala: Long?, idUser: Long?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    var user = medicoRepository.findById(idUser).get()
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.OK && user.user?.id == it?.refUserEntity?.id)
        return true
    }

    return false
  }

  fun checkinDuplicado(idAgendamentoSala: Long?, user: User?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.OK && user == it?.refUserEntity)
        return true
    }

    return false
  }

  fun checkoutDuplicadoProfissional(idAgendamentoSala: Long?, idUser: Long?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    var user = medicoRepository.findById(idUser).get()
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.LIBERADO && user.user?.id == it?.refUserEntity?.id)
        return true
    }

    return false
  }


  fun checkoutDuplicado(idAgendamentoSala: Long?, user: User?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.LIBERADO && user?.id == it?.refUserEntity?.id)
        return true
    }
    return false
  }

  fun checkinDuplicadoAssistente(idAgendamentoSala: Long?, idUser: Long?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    var user = assistenteRepository.findById(idUser).get()
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.OK && user?.user?.id == it?.refUserEntity?.id)
        return true
    }

    return false
  }

  fun checkouDuplicadoAssistente(idAgendamentoSala: Long?, idUser: Long?): Boolean {
    var agendamento = historicoAgendamentoStatusRepository.findByIdAgendamento(idAgendamentoSala)
    var user = assistenteRepository.findById(idUser).get()
    agendamento.forEach {
      if (it?.status == AgendamentoStatus.LIBERADO && user?.user?.id == it?.refUserEntity?.id)
        return true
    }

    return false
  }


  fun confirmaAssistant(idAgendamentoSala: Long?, idUser: Long?): Boolean {

    var agendamento = agendamentoService.findById(idAgendamentoSala).get()

    if (idUser != agendamento?.agendaSalas?.get(0)?.assistente?.id) {

      return false
    }
    return true
  }


  fun validaCheckin(agendamentoId: Long?) {

    var historicos = historicoAgendamentoStatusRepository.findByIdAgendamento(agendamentoId)

    var status = historicos.filter { it?.status == AgendamentoStatus.OK }

    if (status.size >= 2) {

      var agendamento = agendamentoRepository.findById(agendamentoId).get()
      agendamento.agendamentoStatusAtual = AgendamentoStatus.OK

      val save = agendamentoRepository.save(agendamento)
    }

  }

  fun validaCheckout(agendamentoId: Long?) {

    var historicos = historicoAgendamentoStatusRepository.findByIdAgendamento(agendamentoId)

    var status = historicos.filter { it?.status == AgendamentoStatus.LIBERADO }

    if (status.size >= 2) {

      var agendamento = agendamentoRepository.findById(agendamentoId).get()
      agendamento.agendamentoStatusAtual = AgendamentoStatus.LIBERADO

      val save = agendamentoRepository.save(agendamento)
    }
  }

  fun findByIdAgendamento(id: Long?): MutableList<HistoricoAgendamentoStatus?> = historicoAgendamentoStatusRepository.findByIdAgendamento(id)

  @Transactional
  fun saveCheckinDuplo(dto: CheckinDto): HistoricoAgendamentoStatusDto {

    val claims = Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(securityProperties.secret.toByteArray()))
      .build()
      .parseClaimsJws(dto.token?.replace(securityProperties.tokenPrefix, ""))

    var username = claims.body.subject
    var user = userRepository.findByUsernameAtivo(username).get()
    var medicoPat = medicoService.findByrefUserEntity(user)

    if (!medicoPat.isPresent)
      return throw  RuntimeException("Médico não encontrado ! ")

    var medico = medicoPat.get()

    if (medico.status != StatusMedico.APROVADO)
      return throw  RuntimeException("Cadastro precisa ser aprovado para realizar esta ação")

    var confirmaruser = this.confirmaProfissional(dto.agendamentoId, medico.id)
    var checkinDuplicado = this.checkinDuplicado(dto.agendamentoId, user)

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check in do usuário no agendamento")

    if(confirmaruser == false)
      return throw RuntimeException("Usuário diferente do cadastrado no agendamento")

    var agendamento = agendamentoService.findById(dto.agendamentoId).get()

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val horarioAtual = LocalDateTime.now(saoPauloZone)

    if (agendamento.agendaSalas?.first()?.inicio!!.minusMinutes(30).isAfter(horarioAtual))
      return throw RuntimeException("Não é possível realizar o check in antes de 30 minutos para o início do agendamento")

    var dto: HistoricoAgendamentoStatusDto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null,null)
    dto.agendamentoId = agendamento
    dto.userId = medico.user
    dto.dateCreated = horarioAtual
    dto.status = AgendamentoStatus.OK


    val ret = this.save(dto).toDTO()

    var validaCheckin =  this.validaCheckin(agendamento.id)

    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-in(duplo) de profissional realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user, LogEnum.CHECKIN, descricaoLog)

    return ret
  }

  @Transactional
  fun saveCheckin(dto: CheckinDto):HistoricoAgendamentoStatusDto{

    var confirmaruser = this.confirmaProfissional(dto.agendamentoId, dto.userId)
    var checkinDuplicado = this.checkinDuplicadoProfissional(dto.agendamentoId, dto.userId)
    var medico = medicoRepository.findById(dto.userId).get()

    if (medico.status != StatusMedico.APROVADO)
      throw  RuntimeException("Cadastro precisa ser aprovado para realizar esta ação")

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check in do usuário no agendamento")


    if(confirmaruser == false)
      return throw RuntimeException("Usuário diferente do cadastrado no agendamento")

    var user = dto.userId?.let { medicoService.findById(it).get() }
    var agendamento = agendamentoService.findById(dto.agendamentoId).get()

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val horarioAtual = LocalDateTime.now(saoPauloZone)

    if (agendamento.agendaSalas?.first()?.inicio!!.minusMinutes(30).isAfter(horarioAtual))
      return throw RuntimeException("Não é possível realizar o check in antes de 30 minutos para o início do agendamento")

    var dto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null,null )
    dto.agendamentoId = agendamento
    dto.userId = user?.user
    dto.dateCreated = horarioAtual
    dto.status = AgendamentoStatus.OK

    val rel = this.save(dto).toDTO()

    var validaCheckin =  this.validaCheckin(agendamento.id)


    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-in de profissional realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user?.user!!, LogEnum.CHECKIN, descricaoLog)

    return rel
  }

  @Transactional
  fun saveCheckout(dto: CheckinDto):Any{

    var confirmaruser = this.confirmaProfissional(dto.agendamentoId, dto.userId)
    var checkinDuplicado = this.checkoutDuplicadoProfissional(dto.agendamentoId, dto.userId)

    if(confirmaruser == false)
      return throw RuntimeException("Usuário diferente do cadastrado no agendamento")

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check out do usuário no agendamento")




    var user = dto.userId?.let { medicoService.findById(it).get() }
    var agendamento = agendamentoService.findById(dto.agendamentoId).get()
    var dto: HistoricoAgendamentoStatusDto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null,null)

    dto.agendamentoId = agendamento
    dto.userId = user?.user
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    dto.dateCreated = LocalDateTime.now(saoPauloZone)
    dto.status = AgendamentoStatus.LIBERADO

    val ret = this.save(dto).toDTO()
    var validaCheckout =  this.validaCheckout(agendamento.id)


    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-out de profissional realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user?.user!!, LogEnum.CHECKOUT, descricaoLog)

    return ret

  }
  @Transactional
  fun saveCheckinAssistant(dto: CheckinDto):HistoricoAgendamentoStatusDto{

    var confirmaruser = this.confirmaAssistant(dto.agendamentoId, dto.userId)
    var checkinDuplicado = this.checkinDuplicadoAssistente(dto.agendamentoId, dto.userId)

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check in do usuário no agendamento")

    if(confirmaruser == false)
      return throw  RuntimeException("Usuário diferente do cadastrado no agendamento")

    var user = assistenteService.findById(dto.userId).get()
    var agendamento = agendamentoService.findById(dto.agendamentoId).get()

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val horarioAtual = LocalDateTime.now(saoPauloZone)

    if (agendamento.agendaSalas?.first()?.inicio!!.minusMinutes(30).isAfter(horarioAtual))
      return throw RuntimeException("Não é possível realizar o check in antes de 30 minutos para o início do agendamento")

    var dto: HistoricoAgendamentoStatusDto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null,null)
    dto.agendamentoId = agendamento
    dto.userId = user?.user
    dto.dateCreated = horarioAtual
    dto.status = AgendamentoStatus.OK

    val ret = this.save(dto).toDTO()

    var validaCheckin =  this.validaCheckin(agendamento.id)

    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-in de assistente realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user.user!!, LogEnum.CHECKIN, descricaoLog)

    return ret
  }

  @Transactional
  fun saveCheckoutAssistant(dto: CheckinDto):Any{

    var confirmaruser = this.confirmaAssistant(dto.agendamentoId, dto.userId)

    var checkinDuplicado = this.checkouDuplicadoAssistente(dto.agendamentoId, dto.userId)

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check out do usuário no agendamento")

    if(confirmaruser == false){
      return throw  RuntimeException("Usuário diferente do cadastrado no agendamento")
    }
    var user = assistenteService.findById(dto.userId).get()
    var agendamento = agendamentoService.findById(dto.agendamentoId).get()
    var dto: HistoricoAgendamentoStatusDto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null,null)

    dto.agendamentoId = agendamento
    dto.userId = user?.user
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    dto.dateCreated = LocalDateTime.now(saoPauloZone)
    dto.status = AgendamentoStatus.LIBERADO

    val ret = this.save(dto).toDTO()
    var validaCheckout =  this.validaCheckout(agendamento.id)


    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-out de assistente realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user.user!!, LogEnum.CHECKOUT, descricaoLog)

    return ret
  }


  @Transactional
  fun saveCheckoutDuplo(dto: CheckinDto):Any{

    val claims = Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(securityProperties.secret.toByteArray()))
      .build()
      .parseClaimsJws(dto.token?.replace(securityProperties.tokenPrefix, ""))

    var username = claims.body.subject
    var user = userRepository.findByUsernameAtivo(username).get()
    var medicoPat : Optional<Medico> = medicoService.findByrefUserEntity(user)

    if (!medicoPat.isPresent)
      return throw  RuntimeException("Médico não encontrado ! ")

    var medico = medicoPat.get()

    var checkinDuplicado = this.checkoutDuplicado(dto.agendamentoId, user)

    if (checkinDuplicado == true)
      return throw RuntimeException("Já existe um check out do usuário no agendamento")


    var confirmaruser = this.confirmaProfissional(dto.agendamentoId, medico.id)

    if(confirmaruser == false){

      return throw  RuntimeException("Usuário diferente do cadastrado no agendamento")
    }

    var agendamento = agendamentoService.findById(dto.agendamentoId).get()
    var dto: HistoricoAgendamentoStatusDto = HistoricoAgendamentoStatusDto( null,null, null,null,null, null, null)

    dto.agendamentoId = agendamento
    dto.userId = medico.user
    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    dto.dateCreated = LocalDateTime.now(saoPauloZone)
    dto.status = AgendamentoStatus.LIBERADO

    val ret = this.save(dto).toDTO()

    var validaCheckout =  this.validaCheckout(agendamento.id)

    val formatterData = DateTimeFormatter.ofPattern("dd/MM/uuuu")
    val dia = formatterData.format(agendamento.dia)

    val formatterHora = DateTimeFormatter.ofPattern("HH:mm")
    val inicio = formatterHora.format(agendamento.agendaSalas?.first()?.inicio)
    val fim = formatterHora.format(agendamento.agendaSalas?.last()?.fim)


    val descricaoLog = "Check-out(duplo) de profissional realizado na sala '"+ agendamento.agendaSalas?.first()?.sala?.nome +"', agendada para o profissional "+
            agendamento.medico?.user?.nome +" no dia "+ dia +" das "+ inicio +" às "+ fim
    auditLogService.criarLog(user, LogEnum.CHECKOUT, descricaoLog)

    return ret
  }

}
