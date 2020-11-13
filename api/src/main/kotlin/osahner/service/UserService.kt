package osahner.service
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import osahner.config.AppConfiguration
import osahner.domain.Assistente
import osahner.domain.User
import osahner.dto.CreateUserDto
import osahner.dto.UserAdmAssistenteDto
import osahner.dto.UserDto
import osahner.repository.RoleRepository
import osahner.repository.UserRepository
import osahner.utils.Utils
import java.util.*

@Transactional
@Component
class UserService(
  private val userRepository: UserRepository,
  private val appConfiguration: AppConfiguration,
  private val roleRepository : RoleRepository,
  private val emailService: EmailService
) {
  fun list(): Collection<User> = userRepository.findAll()

  fun findById(id: Long?): Optional<User> = userRepository.findById(id)

  fun findByUsername(username: String?): Optional<User> = userRepository.findByUsername(username)

  fun findByUsernameAtivo(username: String?): Optional<User> = userRepository.findByUsernameAtivo(username)

  fun delete(id: Long?) = userRepository.deleteById(id)

  fun save(dto: UserDto): User = userRepository.saveAndFlush(User.fromDTO(dto))

  fun alter(id: Long, userDto: UserDto) : User {
    var safeUser = userDto.copy(id=id)
    return save(safeUser)
  }

  fun existsById(id: Long) = userRepository.existsById(id)

  fun existsByUsername(username: String?): Boolean = userRepository.existsByUsername(username)



  fun existsByRoleName(role: String?) = roleRepository.existsByRoleName(role)

  @Transactional
  fun criarUsuario(userDto: CreateUserDto, roleName: String?): User {

    val role = roleRepository.findByRoleName(roleName).get()

    val user = User()
    user.roles = mutableListOf(role)
    user.nome = userDto.nome
    user.username = userDto.username
    user.password = appConfiguration.bCryptPasswordEncoder().encode(userDto.password)
    user.enabled = true
    user.passwordRecovered = false
    user.passwordExpired = false
    user.accountLocked = false
    user.accountExpired = false
    user.version = 0
    user.ativo = "S"

    return userRepository.save(user)
  }

  @Transactional
  fun criarUsuarioEnviarEmail(userDto: CreateUserDto, roleName: String?): User {

    val role = roleRepository.findByRoleName(roleName).get()

    val user = User()
    user.roles = mutableListOf(role)
    user.nome = userDto.nome
    user.username = userDto.username
    user.password = appConfiguration.bCryptPasswordEncoder().encode(userDto.password)
    user.enabled = true
    user.passwordRecovered = false
    user.passwordExpired = false
    user.accountLocked = true
    user.accountExpired = false
    user.version = 0

    emailService.novoUsuario(userDto.username, userDto.password)

    return userRepository.save(user)
  }

  fun getCurrentUserEmail(): String? = SecurityContextHolder.getContext().authentication.principal as String

  fun updatePassword( newPassword: String? ): ResponseEntity<String> {
    var currentUser = userRepository.findByUsernameAtivo(getCurrentUserEmail()).get()

    if (currentUser == null)
      ResponseEntity.notFound().build<UserDto>()

    currentUser.password = appConfiguration.bCryptPasswordEncoder().encode(newPassword)
    currentUser.passwordRecovered = false
    currentUser.accountLocked = false

    userRepository.saveAndFlush(currentUser)
    return ResponseEntity.ok().build()
  }

  fun recuperarSenha(username: String?): ResponseEntity<String> {
    val user = userRepository.findByUsernameAtivo(username)
    if (user.isPresent){
      val newPassword = Utils.generatePassword()

      var user = user.get()
      user.password = appConfiguration.bCryptPasswordEncoder().encode(newPassword)
      user.passwordRecovered = true

      userRepository.saveAndFlush(user)

      //Envia email de recuperacao
      emailService.recuperarSenha(username, user.nome, newPassword)

      return ResponseEntity.ok().build()
    }else
      return ResponseEntity.notFound().build()
  }


  fun administrativeUsers():List<UserAdmAssistenteDto>{
    var users = userRepository.findAdmAndAtendente()
    users[0].roles?.get(0)?.roleName

    return  users.map { UserAdmAssistenteDto(id = it.id, nome = it.nome, username = it.username, accessType = it.roles?.get(0)?.roleName) }
  }

  fun findAdmOrAtendenteById(id: Long): Optional<User> = userRepository.findAdmOrAtendenteById(id)

  fun alterAdministrativeUser(id: Long, dto: UserAdmAssistenteDto, user: User) : User {
    val role = roleRepository.findByRoleName(dto.accessType).get()

    user.nome = dto.nome
    user.username = dto.username
    user.roles = mutableListOf(role)

    return userRepository.save(user)
  }

  fun atualizarAtivo(userId: Long): User {
    var ativo = "N"
    return saveAtivo(userId, ativo )
  }

  fun saveAtivo(id: Long, ativo: String): User {

    var user: User = userRepository.findById(id).get()

    user.ativo = ativo

    return userRepository.save(user)
  }

}
