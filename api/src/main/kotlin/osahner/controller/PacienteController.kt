package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.*
import osahner.service.MedicoService
import osahner.service.PacienteService
import osahner.service.UserService
import osahner.utils.Utils

@RestController
@RequestMapping("/patient")
class PacienteController(
  private val pacienteService: PacienteService,
  private val userService: UserService,
  private val medicoService: MedicoService
) {
  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PACIENTE', 'ROLE_PROFISSIONAL', 'ROLE_ATENDENTE')")
  fun list() = pacienteService.list().map { it.toCustomDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun getById(@PathVariable id: Long): ResponseEntity<PacienteCustomDto>? = pacienteService.findById(id).map {
    ResponseEntity.ok(it.toCustomDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PACIENTE')")
  fun update(@PathVariable id: Long, @RequestBody pacienteDto: PacienteCustomDto): Any? {
    if (pacienteService.existsById(id)) {

      val user = userService.findByUsername(pacienteDto.username)
      if (user.isPresent && user.get().id != pacienteDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + pacienteDto.username)

      val hasProfissional = medicoService.existsById(pacienteDto.idProfissional!!)
      if (!hasProfissional)
        return throw RuntimeException("Profissional não encontrado")

      val alteredItem = pacienteService.alter(id, pacienteDto).toCustomDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<PacienteCustomDto>()
  }

  @PostMapping
  fun createPatient(@RequestBody createPacienteDto: CreateUserPacienteDto): Any? {
    if (userService.existsByUsername(createPacienteDto.username))
      return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + createPacienteDto.username)

    val profissional = medicoService.findById(createPacienteDto.idProfissional!!).get()
    if (profissional == null)
      return throw RuntimeException("Profissional não encontrado")

    val newPassword = Utils.generatePassword()

    val createUser = CreateUserDto(
      username = createPacienteDto.username,
      password = newPassword,
      nome = createPacienteDto.nome,
      categoria = null
    )
    val newUser = userService.criarUsuarioEnviarEmail(createUser, "ROLE_PACIENTE").toDTO()

    val pacienteDto = PacienteCustomDto(
      id = null,
      idUser = newUser.id,
      nome = newUser.nome,
      username = newUser.username,
      cep = createPacienteDto.cep,
      cpf = createPacienteDto.cpf,
      rg = createPacienteDto.rg,
      telefone = createPacienteDto.telefone,
      endereco = createPacienteDto.endereco,
      imagecpf = createPacienteDto.imagecpf,
      imagerg = createPacienteDto.imagerg,
      idProfissional = createPacienteDto.idProfissional,
      ativo = "S",
      profissional = profissional.toCustomDTO()
    )
    val dto = pacienteService.save(pacienteDto).toDTO()

    return ResponseEntity.status(HttpStatus.CREATED).body(dto)
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun inativarPaciente(@PathVariable id: Long): Any? {
    if (pacienteService.existsById(id)) {
      val alteredItem = pacienteService.atualizarAtivo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()

  }

  @PutMapping("/me")
  @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
  fun updateMe(@RequestBody pacienteDto: PacienteCustomDto): Any? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val paciente = pacienteService.findByrefUserEntity(currentUser).get()

    if (paciente != null) {
      val user = userService.findByUsername(pacienteDto.username)
      if (user.isPresent && user.get().id != pacienteDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + pacienteDto.username)

      val hasProfissional = medicoService.existsById(pacienteDto.idProfissional!!)
      if (!hasProfissional)
        return throw RuntimeException("Profissional não encontrado")

      val alteredItem = pacienteService.alter(paciente.id!!, pacienteDto).toCustomDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoCustomDto>()
  }

  @GetMapping(value = ["/me"])
  @PreAuthorize("hasAuthority('ROLE_PACIENTE')")
  fun getMe(): ResponseEntity<PacienteCustomDto>? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    return pacienteService.findByrefUserEntity(currentUser).map {
      ResponseEntity.ok(it.toCustomDTO())
    }.orElse(ResponseEntity.notFound().build())
  }



}
