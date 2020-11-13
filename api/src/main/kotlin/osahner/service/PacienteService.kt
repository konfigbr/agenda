package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.Paciente
import osahner.domain.User
import osahner.dto.PacienteCustomDto
import osahner.repository.MedicoRepository
import osahner.repository.PacienteRepository
import osahner.repository.UserRepository
import java.util.*

@Component
class PacienteService(
  private val pacienteRepository: PacienteRepository,
  private val userRepository: UserRepository,
  private val medicoRepository: MedicoRepository
) {
  fun list(): Collection<Paciente> = pacienteRepository.findAll()

  fun findById(id: Long?): Optional<Paciente> = pacienteRepository.findById(id)

  fun save(dto: PacienteCustomDto): Paciente {
    var user = userRepository.findById(dto.idUser).get()
    user.nome = dto.nome
    user.username = dto.username

    var profissional = medicoRepository.findById(dto.idProfissional).get()

    val alteredUser =  userRepository.saveAndFlush(user)

    var paciente = Paciente.fromCustomDTO(dto)
    paciente.user = alteredUser
    paciente.profissional = profissional

    return pacienteRepository.saveAndFlush(paciente)
  }

  fun alter(idProfessional: Long, pacienteCustomDto: PacienteCustomDto): Paciente {
    var safePaciente = pacienteCustomDto.copy(id=idProfessional)
    return save(safePaciente)
  }

  fun createNewPaciente(idUser: Long?): Paciente {
    val user = userRepository.findById(idUser).get()

    val paciente = Paciente()
    paciente.user = user

    return pacienteRepository.saveAndFlush(paciente)
  }

  fun existsById(id: Long) = pacienteRepository.existsById(id)

  fun findByrefUserEntity(user: User): Optional<Paciente> = pacienteRepository.findByUser(user)

  fun atualizarAtivo(pacienteId: Long): Paciente {
    var ativo = "N"
    return saveAtivo(pacienteId, ativo )
  }

  fun saveAtivo(id: Long, ativo: String): Paciente {
    var paciente: Paciente = pacienteRepository.findById(id).get()
    var user : User = userRepository.findById(paciente.user?.id).get()
    user.ativo = ativo
    userRepository.save(user)
    paciente.ativo= ativo
    return pacienteRepository.save(paciente)
  }
}
