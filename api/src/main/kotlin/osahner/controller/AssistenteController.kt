package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.*
import osahner.enums.StatusMedico
import osahner.enums.TipoDocumento
import osahner.service.AssistenteService
import osahner.service.UserService
import osahner.utils.Utils

@RestController
@RequestMapping("/assistants")
class AssistenteController(
  private val assistenteService: AssistenteService,
  private val userService: UserService
) {
  @GetMapping(value = ["", "/"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE', 'ROLE_PROFISSIONAL')")
  fun list() = assistenteService.list().map { it.toCustomDTO() }

  @GetMapping(value = ["/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun getById(@PathVariable id: Long): ResponseEntity<AssistenteCustomDto>? = assistenteService.findById(id).map {
    ResponseEntity.ok(it.toCustomDTO())
  }.orElse(ResponseEntity.notFound().build())

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ASSISTENTE')")
  fun update(@PathVariable id: Long, @RequestBody assistenteDto: AssistenteCustomDto): Any? {
    if (assistenteService.existsById(id)) {

      val user = userService.findByUsername(assistenteDto.username)
      if (user.isPresent && user.get().id != assistenteDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + assistenteDto.username)

      val alteredItem = assistenteService.alter(id, assistenteDto).toCustomDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<AssistenteCustomDto>()
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun createAssistant(@RequestBody createAssistenteDto: CreateUserAssistenteDto): Any? {
    if (userService.existsByUsername(createAssistenteDto.username))
      return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + createAssistenteDto.username)

    if (!Utils.checkCpf(createAssistenteDto.cpf!!))
      throw java.lang.RuntimeException("CPF Inválido")

    val newPassword = Utils.generatePassword()

    val createUser = CreateUserDto(
      username = createAssistenteDto.username,
      password = newPassword,
      nome = createAssistenteDto.nome,
      categoria = null
    )
    val newUser = userService.criarUsuarioEnviarEmail(createUser, "ROLE_ASSISTENTE").toDTO()

    val assistenteDto = AssistenteCustomDto(
      id = null,
      idUser = newUser.id,
      nome = newUser.nome,
      username = newUser.username,
      cep = createAssistenteDto.cep,
      cpf = createAssistenteDto.cpf,
      rg = createAssistenteDto.rg,
      anoConclusao = createAssistenteDto.anoConclusao,
      faculdade = createAssistenteDto.faculdade,
      nomeDoCurso = createAssistenteDto.nomeDoCurso,
      telefone = createAssistenteDto.telefone,
      endereco = createAssistenteDto.endereco,
      imagecpf = createAssistenteDto.imagecpf,
      imagerg = createAssistenteDto.imagerg,
      ativo = "S"
    )
    val dto = assistenteService.save(assistenteDto).toDTO()

    return ResponseEntity.status(HttpStatus.CREATED).body(dto)
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun inativarAssistente(@PathVariable id: Long): Any? {
    if (assistenteService.existsById(id)) {
      val alteredItem = assistenteService.atualizarAtivo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoDto>()

  }

  @PutMapping("/me")
  @PreAuthorize("hasAuthority('ROLE_ASSISTENTE')")
  fun updateMe(@RequestBody assistenteDto: AssistenteCustomDto): Any? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    val assistente = assistenteService.findByrefUserEntity(currentUser).get()

    if (assistente != null) {
      val user = userService.findByUsername(assistenteDto.username)
      if (user.isPresent && user.get().id != assistenteDto.idUser)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + assistenteDto.username)

      val alteredItem = assistenteService.alter(assistente.id!!, assistenteDto).toCustomDTO()
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<MedicoCustomDto>()
  }

  @GetMapping(value = ["/me"])
  @PreAuthorize("hasAuthority('ROLE_ASSISTENTE')")
  fun getMe(): ResponseEntity<AssistenteCustomDto>? {
    val currentUser = userService.findByUsername(userService.getCurrentUserEmail()).get()
    return assistenteService.findByrefUserEntity(currentUser).map {
      ResponseEntity.ok(it.toCustomDTO())
    }.orElse(ResponseEntity.notFound().build())

  }
}
