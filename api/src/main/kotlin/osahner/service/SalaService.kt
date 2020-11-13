package osahner.service

import org.springframework.stereotype.Component
import osahner.domain.Especialidade
import osahner.domain.Sala
import osahner.dto.SalaDto
import osahner.repository.SalaRepository
import java.util.*

@Component
class SalaService(
  private val salaRepository: SalaRepository,
  private val especialidadeService: EspecialidadeService,
  private val localService: LocalService
) {
  fun list(): Collection<Sala> = salaRepository.findAll()

  fun findById(id: Long): Optional<Sala> = salaRepository.findById(id)

  fun save(dto: SalaDto): Sala {
    var especialidade = especialidadeService.findById(dto.especialidade?.id).get()
    var local = localService.findById(dto.local?.id).get()

    var sala = Sala.fromDTO(dto)

    sala.refEspecialidadeEntity = especialidade
    sala.refLocalEntity = local

    return salaRepository.saveAndFlush(sala)
  }

  fun delete(id: Long) = salaRepository.deleteById(id)

  fun alter(id: Long, salaDto: SalaDto) : Sala {
    var safeSala = salaDto.copy(id=id)
    return save(safeSala)
  }

  fun existsById(id: Long) = salaRepository.existsById(id)
}
