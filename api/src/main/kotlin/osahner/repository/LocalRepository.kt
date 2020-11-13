package osahner.repository

import org.springframework.data.jpa.repository.JpaRepository
import osahner.domain.Local
import osahner.domain.TipoEquipamento
import java.util.*

interface LocalRepository : JpaRepository<Local, Long> {
  fun existsByNome(nome: String?): Boolean
  fun findByNome(nome: String?): Optional<Local>
}
