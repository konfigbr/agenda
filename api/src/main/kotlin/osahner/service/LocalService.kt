package osahner.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import osahner.domain.Local
import osahner.dto.LocalDto
import osahner.repository.LocalRepository
import java.util.*

@Component
class LocalService( private val localRepository: LocalRepository) {
  fun list(): Collection<Local> = localRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))

  fun findById(id: Long?): Optional<Local> = localRepository.findById(id)

  fun findByNome(nome: String?): Optional<Local> = localRepository.findByNome(nome)

  fun save(dto: LocalDto): Local = localRepository.saveAndFlush(Local.fromDTO(dto))

  fun delete(id: Long) = localRepository.deleteById(id)

  fun alter(id: Long, local: LocalDto) : Local {
    var safeLocal = local.copy(id=id)
    return save(safeLocal)
  }

  fun existsById(id: Long) = localRepository.existsById(id)

  fun existsByNome(nome: String?) = localRepository.existsByNome(nome)
}
