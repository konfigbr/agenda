package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import osahner.dto.*
import osahner.service.*
import osahner.utils.Utils

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService,
        private val medicoService: MedicoService,
        private val assistenteService: AssistenteService,
        private val emailService: EmailService,
        private val exportPdfService: ExportPdfService
) {
  @PostMapping("/update-password")
  fun updatePassword(@RequestBody userDto: UserDto): ResponseEntity<String> {
    return userService.updatePassword(userDto.password)
  }

  @PostMapping("/recover-password")
  fun recuperarSenha(@RequestBody userDto: UserDto): ResponseEntity<String> {
    return userService.recuperarSenha(userDto.username)
  }

  @GetMapping("/administrative-users")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun listarAdministrativeUsers():List<UserAdmAssistenteDto> {
    return userService.administrativeUsers()
  }

  @PostMapping("/administrative-users")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun createAdministrativeUser(@RequestBody createUserDto: CreateUserDto): Any? {
    if (userService.existsByUsername(createUserDto.username))
      return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + createUserDto.username)

    if (createUserDto.accessType != "ROLE_ADMINISTRADOR" && createUserDto.accessType != "ROLE_ATENDENTE")
      return throw RuntimeException("AccessType(Role) inválida!")

    val newPassword = Utils.generatePassword()
    createUserDto.password = newPassword

    val user = userService.criarUsuarioEnviarEmail(createUserDto, createUserDto.accessType)
    val createdUser = UserAdmAssistenteDto(
      username = user.username,
      nome = user.nome,
      accessType = user.roles?.get(0)?.roleName,
      id = user.id
    )
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
  }

  @PutMapping("/administrative-users/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_ATENDENTE')")
  fun updateAdministrativeUser(@PathVariable id: Long, @RequestBody dto: UserAdmAssistenteDto): Any? {
    val result = userService.findAdmOrAtendenteById(id)
    if (result.isPresent) {
      var admUser = result.get()

      val user = userService.findByUsername(dto.username)
      if (user.isPresent && user.get().id != id)
        return throw RuntimeException("Já existe um usuário cadastrado com o e-mail: " + dto.username)

      if(dto.accessType != "ROLE_ADMINISTRADOR" && dto.accessType != "ROLE_ATENDENTE")
        return throw RuntimeException("AccessType(Role) inválida!")

        val alteredUser = userService.alterAdministrativeUser(id, dto, admUser)
        val createdUser = UserAdmAssistenteDto(
          username = alteredUser.username,
          nome = alteredUser.nome,
          accessType = alteredUser.roles?.get(0)?.roleName,
          id = alteredUser.id
        )

      return ResponseEntity.ok(createdUser)
    }
    return ResponseEntity.notFound().build<MedicoCustomDto>()
  }

  @GetMapping(value = ["/administrative-users/{id}"])
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_ATENDENTE')")
  fun getById(@PathVariable id: Long): ResponseEntity<UserAdmAssistenteDto>? = userService.findAdmOrAtendenteById(id).map {
    val createdUser = UserAdmAssistenteDto(
      username = it.username,
      nome = it.nome,
      accessType = it.roles?.get(0)?.roleName,
      id = it.id
    )

    ResponseEntity.ok(createdUser)
  }.orElse(ResponseEntity.notFound().build())

  @PostMapping("/teste-envio-email/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
  fun testeEnvioEmail(@PathVariable id: Long) {

    val profissional = medicoService.findById(id).get()
    var ret = exportPdfService.exportTerms(profissional)

    emailService.enviarTermoAceite("andre.dionisio@hdnit.com.br", "André", id, ret)
  }

  @DeleteMapping("/administrative-users/{id}")
  @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_PROFISSIONAL')")
  fun inativarAssistente(@PathVariable id: Long): Any? {

    if (userService.existsById(id)) {
      val user = userService.findById(id).get()
      if(user.roles?.first()?.roleName == "ROLE_PROFISSIONAL" || user.roles?.first()?.roleName == "ROLE_ASSISTENTE" )
        return ResponseEntity.notFound().build<UserDto>()

      val alteredItem = userService.atualizarAtivo(id)
      return ResponseEntity.ok(alteredItem)
    }
    return ResponseEntity.notFound().build<UserDto>()

  }


}
