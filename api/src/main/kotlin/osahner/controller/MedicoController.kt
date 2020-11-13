package osahner.controller

import org.apache.poi.ss.usermodel.Workbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import osahner.domain.Consulta

import osahner.dto.*
import osahner.enums.*
import osahner.service.*
import osahner.utils.Utils
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/professionals")
class MedicoController(
  private val userService: UserService,
  private val medicoService: MedicoService,
  private val especialidadeService: EspecialidadeService,
  private val poiExportService: PoiExportService,
  private val agendamentoAssistenteService: AgendamentoAssistenteService,
  private val emailService: EmailService,
  private val exportPdfService: ExportPdfService,
  private val auditLogService: AuditLogService,
  private val consultaService: ConsultaService,
  private val pacienteService: PacienteService,
  private val documentoProfissionalService: DocumentoProfissionalService
) {

  @PostMapping("/sign-up")
  @Transactional
  fun createNewProfessional(@RequestBody user: CreateUserDto): Any? {
    if (userService.existsByUsername(user.username))
      return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + user.username)

    if (user.tipoDocumento == TipoDocumento.CPF && !Utils.checkCpf(user.documento!!))
      throw java.lang.RuntimeException("CPF Inválido")
    else if (user.tipoDocumento == TipoDocumento.CNPJ && !Utils.checkCnpj(user.documento!!))
      throw java.lang.RuntimeException("CNPJ Inválido")

    val newUser = userService.criarUsuario(user, "ROLE_PROFISSIONAL").toDTO()

    medicoService.createNewProfessional(newUser.id, user.especialidade, user.documento, user.tipoDocumento)

    return ResponseEntity.status(HttpStatus.CREATED).body(newUser)
  }

  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE', 'ROLE_ATENDENTE')")
  fun list() = medicoService.list().map { it.toCustomDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE')")
  fun getById(@PathVariable id: Long): ResponseEntity<MedicoCustomDto>? = medicoService.findById(id).map {
    ResponseEntity.ok(it.toCustomDTO())
  }.orElse(ResponseEntity.notFound().build())

  @GetMapping(value = ["/me/medical-appointments"])
  @PreAuthorize("hasAnyAuthority('ROLE_PROFISSIONAL')")
  fun listMedicalAppointments(
          @RequestParam(required = true) dataInicio: String,
          @RequestParam(required = true) dataFim: String,
          @RequestParam salaId: Long?,
          @RequestParam status: StatusConsulta?,
          @RequestParam pacienteId: Long?
  ): ResponseEntity<MutableList<ConsultaDto>> {
    val consultas = consultaService.listMyMedicalAppointments(
            LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_DATE_TIME),
            LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_DATE_TIME),
            salaId,
            status,
            pacienteId
    ).map { it.toDTO() }.toMutableList()

    return ResponseEntity.ok(consultas)
  }

  @PostMapping(value = ["/{medicoId}/medical-appointments"])
  @PreAuthorize("hasAnyAuthority('ROLE_PACIENTE')")
  fun saveMedicalAppointment(@PathVariable medicoId: Long, @Valid @RequestBody createConsultaDto: CreateConsultaDto) :
    ResponseEntity<ConsultaDto> {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val paciente = pacienteService.findByrefUserEntity(currentUser).get()

    val consulta = consultaService.saveMedicalAppointment(medicoId, createConsultaDto,paciente)
    return ResponseEntity.ok(consulta.toDTO())
  }

  @GetMapping(value = ["/url/{url}"])
  fun getByUrl(@PathVariable url: String): ResponseEntity<MedicoCustomDto>? = medicoService.findByUrl(url).map {
    ResponseEntity.ok(it.toCustomDTO())
  }.orElse(ResponseEntity.notFound().build())

  @GetMapping(value = ["/specialties/{idEspecialidade}"])
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun getByEspecialidade(@PathVariable idEspecialidade: Long): Any? {
    val especialidade = especialidadeService.findById(idEspecialidade)

    return medicoService.findByRefEspecialidadeEntity(especialidade.get()).map { it.toCustomDTO() }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun update(@PathVariable id: Long, @RequestBody medicoDto: MedicoCustomDto): Any? {

    val medico = medicoService.findById(id).get()
    if (medico != null) {
      val user = userService.findByUsername(medicoDto.username)
      if (user.isPresent && user.get().id != medicoDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + medicoDto.username)

      if (medico.url != null && medico.url != medicoDto.url)
        return throw RuntimeException("Não é possível editar a URL após ter sido criada")

      if (medicoDto.url != null){
        val profissional = medicoService.findByUrl(medicoDto.url!!)
        if (profissional.isPresent && profissional.get().id != id)
          return throw RuntimeException("Já existe um usuário cadastrado com a URL: " + medicoDto.url)

        val regex = Regex("^[0-9a-zA-Z-]+$")
        val validacao = regex.containsMatchIn(medicoDto.url!!)

        if (!validacao)
          return throw RuntimeException("URL não pode conter espaços e caracteres especiais")
      }

      medicoDto.status = StatusMedico.AGUARDANDO_APROVAÇÃO
      medicoDto.ativo = "S"

      val alteredItem = medicoService.alter(id, medicoDto)

      if (medico.termoUsoPrivacidade == StatusTermoDeUso.RECUSADO || medico.termoUsoPrivacidade == null) {
        if (alteredItem.termoUsoPrivacidade == StatusTermoDeUso.ACEITO) {

          var ret = exportPdfService.exportTerms(alteredItem)

          emailService.enviarTermoAceite(alteredItem.user?.username, alteredItem.user?.nome, alteredItem.id, ret)
        }
      }
      val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()

      val descricaoLog = "Atualização de dados do perfil do profissional "+ alteredItem.user?.nome +" (idProfissional="+
              alteredItem.id+")"
      auditLogService.criarLog(currentUser, LogEnum.EDICAO_PERFIL, descricaoLog)

      return ResponseEntity.ok(alteredItem.toCustomDTO())
    }
    return ResponseEntity.notFound().build<MedicoCustomDto>()
  }

  @PutMapping("/me")
  @PreAuthorize("hasAuthority('ROLE_PROFISSIONAL')")
  fun updateMe(@RequestBody medicoDto: MedicoCustomDto): Any? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val medico = medicoService.findByrefUserEntity(currentUser).get()

    if (medico != null) {
      val user = userService.findByUsername(medicoDto.username)
      if (user.isPresent && user.get().id != medicoDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + medicoDto.username)

      if (medico.url != null && medico.url != medicoDto.url)
        return throw RuntimeException("Não é possível editar a URL após ter sido criada")

      if (medicoDto.url != null){
        val regex = Regex("^[0-9a-zA-Z-]+$")
        val validacao = regex.containsMatchIn(medicoDto.url!!)

        if (!validacao)
          return throw RuntimeException("URL não pode conter espaços e caracteres especiais")
      }

      val profissional = medicoService.findByUrl(medicoDto.url!!)
      if (profissional.isPresent && profissional.get().id != medicoDto.id)
        return throw RuntimeException("Já existe um usuário cadastrado com a URL: " + medicoDto.url)

      medicoDto.status = StatusMedico.AGUARDANDO_APROVAÇÃO
      val alteredItem = medicoService.alter(medico.id!!, medicoDto).toCustomDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoCustomDto>()
  }

  @GetMapping(value = ["/me"])
  @PreAuthorize("hasAuthority('ROLE_PROFISSIONAL')")
  fun getMe(): ResponseEntity<MedicoCustomDto>? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    return medicoService.findByrefUserEntity(currentUser).map {
      ResponseEntity.ok(it.toCustomDTO())
    }.orElse(ResponseEntity.notFound().build())
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun createprofessional(@RequestBody createMedicoDto: CreateUserMedicoDto): Any? {
    if (userService.existsByUsername(createMedicoDto.username))
      return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + createMedicoDto.username)

    if (medicoService.existsByCpf(createMedicoDto.cpf))
      return throw RuntimeException("Já existe um usuário cadastrado com o CPF: " + createMedicoDto.cpf)

    if (medicoService.existsByCrm(createMedicoDto.crm))
      return throw RuntimeException("Já existe um usuário cadastrado com o CRM: " + createMedicoDto.crm)

    if (medicoService.existsByUrl(createMedicoDto.url))
      return throw RuntimeException("Já existe um usuário cadastrado com a URL: " + createMedicoDto.url)

    if (createMedicoDto.tipoDocumento == TipoDocumento.CPF && !Utils.checkCpf(createMedicoDto.cpf!!))
      throw java.lang.RuntimeException("CPF Inválido")
    else if (createMedicoDto.tipoDocumento == TipoDocumento.CNPJ && !Utils.checkCnpj(createMedicoDto.cpf!!))
      throw java.lang.RuntimeException("CNPJ Inválido")

    val newPassword = Utils.generatePassword()

    val createUser = CreateUserDto(
      username = createMedicoDto.username,
      password = newPassword,
      nome = createMedicoDto.nome
    )
    val newUser = userService.criarUsuarioEnviarEmail(createUser, "ROLE_PROFISSIONAL").toDTO()

    val saoPauloZone = java.time.ZoneId.of("America/Sao_Paulo")
    val now = LocalDateTime.now(saoPauloZone)

    val medicoDto = MedicoCustomDto(
      id = null,
      idUser = newUser.id,
      nome = newUser.nome,
      username = newUser.username,
      cep = createMedicoDto.cep,
      cpf = createMedicoDto.cpf,
      crm = createMedicoDto.crm,
      rg = createMedicoDto.rg,
      telefone = createMedicoDto.telefone,
      especialidade = createMedicoDto.especialidade,
      ufcrm = createMedicoDto.ufcrm,
      endereco = createMedicoDto.endereco,
      imagecpf = createMedicoDto.imagecpf,
      imagecrm = createMedicoDto.imagecrm,
      imagerg = createMedicoDto.imagerg,
      status = StatusMedico.AGUARDANDO_APROVAÇÃO,
      imageFoto = createMedicoDto.imageFoto,
      ativo = "S",
      url = createMedicoDto.url,
      tipoDocumento = createMedicoDto.tipoDocumento,
      termoAceite = createMedicoDto.termoAceite,
      imageComprovante = createMedicoDto.imageComprovante,
      termoUsoPrivacidade = createMedicoDto.termoUsoPrivacidade,
      numero = createMedicoDto.numero,
      logradouro = createMedicoDto.logradouro,
      cidade = createMedicoDto.cidade,
      bairro = createMedicoDto.bairro,
      nacionalidade = createMedicoDto.nacionalidade,
      estadoCivil = createMedicoDto.estadoCivil,
      uf = createMedicoDto.uf,
      dateCreated = now,
      hasDescontoVigente = null
    )
    val dto = medicoService.save(medicoDto).toDTO()

    return ResponseEntity.status(HttpStatus.CREATED).body(dto)
  }

  @PutMapping("/approve-professional/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
  fun confirmarMedicoPeloAdm(@PathVariable id: Long): Any? {
    if (medicoService.existsById(id)) {
      val alteredItem = medicoService.alterStatusAprovado(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()
  }

  @PutMapping("/reprove-professional/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
  fun reprovarMedicoPeloAdm(@PathVariable id: Long): Any? {
    if (medicoService.existsById(id)) {
      val alteredItem = medicoService.alterStatusReprovado(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()
  }

  @GetMapping(value = ["/pending"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR')")
  fun listProfissionaisPendentes() = medicoService.listAprovacao().map { it.toCustomDTO() }

  @PutMapping("/termo-uso-privacidade/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun termoUsoPrivacidade(@PathVariable id: Long): Any? {
    if (medicoService.existsById(id)) {
      val alteredItem = medicoService.atualizarTermo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()
  }

  @PutMapping("/negar-termo-uso-privacidade/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR, ROLE_PROFISSIONAL')")
  fun negarTermoUsoPrivacidade(@PathVariable id: Long): Any? {
    if (medicoService.existsById(id)) {
      val alteredItem = medicoService.negarTermo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()
  }

  @GetMapping(value = ["/pesquisar-profissionais/{especialidadeId}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_PROFISSIONAL')")
  fun pesquisarMedicos(@PathVariable especialidadeId: Long): MutableList<RelatorioProfissionaisDto> = medicoService.pesquisarMedicos(especialidadeId)

  @GetMapping(value = ["/export-profissionais"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun export(@RequestParam especialidadeId: String?): ResponseEntity<ByteArrayResource> {
    val headers = HttpHeaders().apply {
      add("Content-Disposition", "filename=\"Export-${Date().time}.xls\"")
    }

    val bos = ByteArrayOutputStream()
    bos.use {
      this.toWorkbook(especialidadeId).apply {
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


  fun toWorkbook(especialidadeId: String?): Workbook {
    var dtos: MutableList<AddressDto> = mutableListOf()
    val result = medicoService.pesquisarMedicos(especialidadeId?.toLong())
    val format = SimpleDateFormat("dd/MM/yyy")
    val especialidade = especialidadeService.findById(especialidadeId?.toLong()).get()

    return poiExportService.buildExcelDocumentProfissionais(
      "Export Relatório Profissionais",
      especialidade.nome,
      listOf("nome", "rg", "cpf", "especialidade", "documentoProfissional", "telefone", "email"),
      listOf("Nome", "RG", "CPF", "Especialidade", "Documento Profissional", "Telefone", "E-mail"),
      result
    )
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun inativarProfissional(@PathVariable id: Long): Any? {
    if (medicoService.existsById(id)) {
      val alteredItem = medicoService.atualizarAtivo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()
  }

  @GetMapping(value = ["/current-scheduling"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun atual(): Any? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val medico = medicoService.findByrefUserEntity(currentUser).get()

    if(medico == null)
      return ResponseEntity.notFound().build<MedicoDto>()

    return ResponseEntity.ok(agendamentoAssistenteService.agendamentoAtualProfessional(medico.id!!))
  }

  @GetMapping(value = ["/terms-of-use"])
  fun getTermosDeUso() = medicoService.getTermosDeUso().map { it.toDTO() }

  @GetMapping(value = ["/withHasDiscounts"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE', 'ROLE_ATENDENTE')")
  fun listWithDescontoVigente() = medicoService.listWithDescontoVigente()

  @GetMapping(value = ["/findByCfm"])
  fun findByCfm(@RequestParam inscricao: String, uf: String): ResponseEntity<String> =
          ResponseEntity.ok(documentoProfissionalService.connectCfm(inscricao, uf))

  @GetMapping(value = ["/findByCfo"])
  fun findByCfo(@RequestParam inscricao: String, uf: String, categoria: String): ResponseEntity<String> =
          ResponseEntity.ok(documentoProfissionalService.connectCfo(inscricao, uf, categoria))
}
