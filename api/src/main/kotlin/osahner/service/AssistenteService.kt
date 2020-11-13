package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.Assistente
import osahner.domain.Medico
import osahner.domain.User
import osahner.dto.AssistenteCustomDto
import osahner.dto.AssistenteDto
import osahner.dto.MedicoDto
import osahner.repository.AssistenteRepository
import osahner.repository.UserRepository
import osahner.utils.Utils
import java.util.*

@Component
class AssistenteService(
  private val assistenteRepository: AssistenteRepository,
  private val userRepository: UserRepository
) {
  fun list(): Collection<Assistente> = assistenteRepository.findAll()

  fun findById(id: Long?): Optional<Assistente> = assistenteRepository.findById(id)

  fun save(dto: AssistenteCustomDto): Assistente {
    var user = userRepository.findById(dto.idUser).get()
    user.nome = dto.nome
    user.username = dto.username

    if (!Utils.checkCpf(dto.cpf!!))
      throw java.lang.RuntimeException("CPF Inválido")

    val alteredUser =  userRepository.saveAndFlush(user)

    var assistente = Assistente.fromCustomDTO(dto)
    assistente.user = alteredUser

    return assistenteRepository.saveAndFlush(assistente)
  }

  fun alter(idProfessional: Long, assistenteCustomDto: AssistenteCustomDto): Assistente {
    var safeAssistente = assistenteCustomDto.copy(id=idProfessional)
    return save(safeAssistente)
  }

  fun createNewAssistente(idUser: Long?): Assistente {
    val user = userRepository.findById(idUser).get()

    val assistente = Assistente()
    assistente.user = user

    return assistenteRepository.saveAndFlush(assistente)
  }

  fun existsById(id: Long) = assistenteRepository.existsById(id)

  fun findByrefUserEntity(user: User): Optional<Assistente> = assistenteRepository.findByUser(user)

  fun atualizarAtivo(assistenteId: Long): Assistente {
    var ativo = "N"
    return saveAtivo(assistenteId, ativo )
  }

  fun saveAtivo(id: Long, ativo: String): Assistente {

    var assistente: Assistente = assistenteRepository.findById(id).get()
    var user : User = userRepository.findById(assistente.user?.id).get()
    user.ativo = ativo
    userRepository.save(user)
    assistente.ativo= ativo
    return assistenteRepository.save(assistente)
  }
}
