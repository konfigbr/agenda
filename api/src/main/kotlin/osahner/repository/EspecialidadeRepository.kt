package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Especialidade
import osahner.domain.Local
import java.util.*

interface EspecialidadeRepository : JpaRepository<Especialidade, Long> {
  fun existsByNome(nome: String?): Boolean
  fun findByNome(nome: String?): Optional<Especialidade>
  fun findByExibirCadastroProfissionalOrderByNomeAsc(exibirCadastroProfissional: Boolean): Collection<Especialidade>
}
