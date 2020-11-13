package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.Especialidade
import osahner.dto.EspecialidadeDto
import osahner.repository.EspecialidadeRepository
import java.util.*

@Component
class EspecialidadeService( private val especialidadeRepository: EspecialidadeRepository) {
  fun list(): Collection<Especialidade> = especialidadeRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

  fun listProfessionalSpecialties(): Collection<Especialidade> = especialidadeRepository.findByExibirCadastroProfissionalOrderByNomeAsc(true)

  fun findById(id: Long?): Optional<Especialidade> = especialidadeRepository.findById(id)

  fun findByNome(nome: String?): Optional<Especialidade> = especialidadeRepository.findByNome(nome)

  fun save(dto: EspecialidadeDto): Especialidade = especialidadeRepository.saveAndFlush(Especialidade.fromDTO(dto))

  fun delete(id: Long) = especialidadeRepository.deleteById(id)

  fun alter(id: Long, especialidade: EspecialidadeDto) : Especialidade {
    var safeEspecialidade = especialidade.copy(id=id)
    return save(safeEspecialidade)
  }

  fun existsById(id: Long) = especialidadeRepository.existsById(id)

  fun existsByNome(nome: String?) = especialidadeRepository.existsByNome(nome)
}
